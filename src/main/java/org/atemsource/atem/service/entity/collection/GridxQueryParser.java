package org.atemsource.atem.service.entity.collection;

import java.io.IOException;
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
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class GridxQueryParser<O,T> implements QueryParser<O,T> {
	
	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	private ObjectMapper objectMapper;
	
	@Override
	public  Query parseQuery(HttpServletRequest req, CollectionResource<O,T> resource) {
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
						String attributeCode = ((ObjectNode) operands.get(0))
								.get("data").getTextValue();
						SingleAttribute<?> attribute = (SingleAttribute<?>) resource.getOriginalType()
								.getAttribute(attributeCode);
						if (attribute==null) {
							throw new IllegalArgumentException("cannot find attribute '"+attributeCode+"'");
						}
						Object value = JsonUtils
								.convertToJava(((ObjectNode) operands.get(1))
										.get("data"));
						predicates.add(new AttributePredicate(attribute,
								operator, value));
					}
					query = new Query(or, predicates);
				}
			} catch (JsonProcessingException e) {
				throw new TechnicalException("cannot parse query", e);
			} catch (IOException e) {
				throw new TechnicalException("cannot parse query", e);
			}
		} else {
			List<AttributePredicate<?>> predicates = new ArrayList<AttributePredicate<?>>();
			Iterator<Map.Entry<String, String[]>> iterator = req
					.getParameterMap().entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry<String, String[]> next = iterator.next();
				String attributeCode = next.getKey();
				SingleAttribute<?> attribute = (SingleAttribute<?>) resource.getOriginalType()
						.getAttribute(attributeCode);
				if (attribute != null
						&& attribute.getTargetType() instanceof PrimitiveType) {
					Object value = ((PrimitiveType) attribute.getTargetType())
							.deserialize(next.getValue()[0]);
					Operator operator;
					if (value instanceof String) {
						String text = (String) value;
						if (text.endsWith("*")) {
							operator = Operator.LIKE;
							value = text.substring(0, text.length() - 1);
						} else {
							operator = Operator.EQUAL;
						}
					} else {
						operator = Operator.EQUAL;
					}
					predicates.add(new AttributePredicate(attribute, operator,
							value));
				}
			}
			query = new Query(false, predicates);

		}
		return query;

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
