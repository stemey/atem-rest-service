package org.atemsource.atem.service.entity.collection;

import java.io.IOException;
import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang.StringUtils;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.api.type.PrimitiveType;
import org.atemsource.atem.impl.json.JsonUtils;
import org.atemsource.atem.service.entity.search.AttributePredicate;
import org.atemsource.atem.service.entity.search.Operator;
import org.atemsource.atem.service.entity.search.Query;
import org.atemsource.atem.service.refresolver.CollectionResource;
import org.atemsource.atem.utility.transform.impl.transformation.AbstractOneToOneAttributeTransformation;
import org.atemsource.atem.utility.transform.impl.transformation.OneToOneAttributeTransformation;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class GridxQueryParser<O, T> implements QueryParser<O, T> {

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	private ObjectMapper objectMapper;

	@Override
	public Query parseQuery(HttpServletRequest req,
			CollectionResource<O, T> resource) {
		// resource.getTransformation();
		Query query = null;
		String queryString = req.getParameter("query");
		if (StringUtils.isNotEmpty(queryString)) {
			try {
				ObjectNode node = (ObjectNode) objectMapper
						.readTree(queryString);
				JsonNode jsonNode = node.get("op");
				if (jsonNode != null && !jsonNode.isNull()) {
					List<AttributePredicate<?>> predicates = new ArrayList<AttributePredicate<?>>();
					boolean or = jsonNode.getTextValue().equals("or");
					ArrayNode predicateNodes = (ArrayNode) node.get("data");
					Iterator<JsonNode> iterator = predicateNodes.iterator();
					while (iterator.hasNext()) {
						ObjectNode next = (ObjectNode) iterator.next();
						Operator operator = parseOperator(next.get("op")
								.getTextValue());
						ArrayNode operands = (ArrayNode) next.get("data");
						Object value = JsonUtils
								.convertToJava(((ObjectNode) operands.get(1))
										.get("data"));
						String attributeCode = ((ObjectNode) operands.get(0))
								.get("data").getTextValue();
						AbstractOneToOneAttributeTransformation<?, ?> attributeTransformation = getAttribute(
								resource, attributeCode);
						Object convertedValue = convertValue(attributeTransformation,
								(Serializable) value);
						if (attributeTransformation == null) {
							throw new IllegalArgumentException(
									"cannot find attribute '" + attributeCode
											+ "'");
						}
						predicates.add(new AttributePredicate(
								(SingleAttribute) attributeTransformation.getAttributeA()
										.getAttribute(), operator,
								convertedValue));
					}
					query = new Query(or, predicates);
				}
			} catch (JsonProcessingException e) {
				throw new TechnicalException("cannot parse query", e);
			} catch (IOException e) {
				throw new TechnicalException("cannot parse query", e);
			}
		} else if (req.getParameterMap().size() > 0) {
			List<AttributePredicate<?>> predicates = new ArrayList<AttributePredicate<?>>();
			Iterator<Map.Entry<String, String[]>> iterator = req
					.getParameterMap().entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String[]> next = iterator.next();
				String attributeCode = next.getKey();
				AbstractOneToOneAttributeTransformation<?, ?> transformation = getAttribute(
						resource, attributeCode);
				if (transformation != null
						&& transformation.getAttributeB().getTargetType() instanceof PrimitiveType) {

					if (next.getValue().length > 1) {
						Operator operator = Operator.IN;
						convertArray(transformation, next.getValue());
						predicates.add(new AttributePredicate(
								(SingleAttribute) transformation
										.getAttributeB().getAttribute(),
								operator, next.getValue()));
					} else {
						Object value = convertValue(transformation,
								next.getValue()[0]);

						Operator operator;
						if (value instanceof String) {
							String text = (String) value;
							if (text.endsWith("*")) {
								if (text.length() > 1) {
									operator = Operator.LIKE;
									value = text.replace('*', '%');
								} else {
									operator = null;
								}
							} else {
								operator = Operator.EQUAL;
							}
						} else {
							operator = Operator.EQUAL;
						}
						if (operator != null) {
							predicates.add(new AttributePredicate(
									(SingleAttribute) transformation
											.getAttributeB().getAttribute(),
									operator, value));
						}
					}
				}
			}
			query = new Query(false, predicates);

		}
		return query;

	}

	private <A, B> String[] convertArray(
			AbstractOneToOneAttributeTransformation<A, B> transformation,
			String[] value) {
		String[] result = new String[value.length];
		for (int i = 0; i < value.length; i++) {
			result[i] = (String) convertValue(transformation, value[0]);
		}
		return result;

	}

	private <A, B> A convertValue(
			AbstractOneToOneAttributeTransformation<A, B> transformation,
			Serializable value) {
		B in = ((PrimitiveType<B>) transformation.getAttributeB()
				.getAttribute().getTargetType()).deserialize(value);
		return transformation.convertBA(in, null);
	}

	private <A, B> AbstractOneToOneAttributeTransformation<A, B> getAttribute(
			CollectionResource resource, String attributeCode) {
		return resource.getTransformation().getAttributeTransformationByB(
				attributeCode);
	}

	private Operator parseOperator(String op) {
		if (op.startsWith("greater")) {
			if (endswithEqual(op)) {
				return Operator.GET;
			} else {
				return Operator.GT;
			}
		} else if (op.startsWith("less")) {
			if (endswithEqual(op)) {
				return Operator.LET;
			} else {
				return Operator.LT;
			}
		} else if (op.equals("equal")) {
			return Operator.EQUAL;
		} else if (op.equals("contain")) {
			return Operator.LIKE;
		} else {
			throw new IllegalArgumentException("unknown operator " + op);
		}
	}

	private boolean endswithEqual(String op) {
		return op.endsWith("Equal");
	}
}
