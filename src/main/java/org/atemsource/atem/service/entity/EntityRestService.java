package org.atemsource.atem.service.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.api.service.DeletionService;
import org.atemsource.atem.api.service.IdentityService;
import org.atemsource.atem.api.service.PersistenceService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.json.JsonUtils;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.service.entity.search.AttributePredicate;
import org.atemsource.atem.service.entity.search.AttributeSorting;
import org.atemsource.atem.service.entity.search.Operator;
import org.atemsource.atem.service.entity.search.Paging;
import org.atemsource.atem.service.entity.search.Query;
import org.atemsource.atem.service.entity.search.Sorting;
import org.atemsource.atem.service.meta.service.binding.editor.DojoTableTransformationFactory;
import org.atemsource.atem.service.meta.service.provider.MetaProvider;
import org.atemsource.atem.utility.transform.api.ConverterFactory;
import org.atemsource.atem.utility.transform.api.JacksonTransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.converter.ConverterUtils;
import org.atemsource.atem.utility.validation.SimpleValidationContext;
import org.atemsource.atem.utility.validation.ValidationService;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.JsonProcessingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class EntityRestService {

	private static final String CONTENT_RANGE_HEADER = "Content-Range";
	public static final String REST_PATTERN = "/([^/]+)(/([^/]+))?";
	public static final Pattern CONTENT_RANGE_PATTERN = Pattern.compile("items=([0-9]+)-([0-9]+)?");
	public static final Pattern SORTING_PATTERN = Pattern.compile("( |\\-)([a-zA-Z0-9]+)");
	private static Logger logger = Logger.getLogger(EntityRestService.class);
	private Attribute derivedTypeAttribute;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private ConverterFactory idConverterfactory;

	private EntityType<EntityType> metaType;

	private ObjectMapper objectMapper;

	private Pattern singleResourcePattern;

	private final String uriPrefix = "/entity/entities";

	private Serializable convertId(String idAsString, EntityType<?> targetType) {
		// TODO we need to use the type of the transformed attribute
		IdentityService identityService = targetType.getService(IdentityService.class);
		Class<?> javaType = identityService.getIdType(targetType).getJavaType();
		return (Serializable) PrimitiveConverterUtils.fromString(javaType, idAsString);
	}

	ObjectNode createErrorNode(ReturnErrorObject returnErrorObject) {
		ObjectNode error = objectMapper.createObjectNode();
		ArrayNode errors = objectMapper.createArrayNode();
		error.put("errors", errors);
		for (org.atemsource.atem.utility.validation.AbstractValidationContext.Error e : returnErrorObject.getErrors()) {
			ObjectNode singleError = objectMapper.createObjectNode();
			singleError.put("path", e.getPath());
			singleError.put("message", e.getMessage());
			errors.add(singleError);
		}
		return error;
	}

	public static class Result {
		private ArrayNode entities;
		private long totalCount;
	}

	private String sortParam = "sortBy";

	public TypeAndId parse(String uri) {
		TypeAndId typeAndId;
		Matcher matcher = singleResourcePattern.matcher(uri);
		if (matcher.find()) {
			String type = matcher.group(1);
			EntityType<Object> entityType = entityTypeRepository.getEntityType(type);

			String idAsString = matcher.group(2);

			if (idAsString == null) {
				typeAndId = new TypeAndId(entityType, null);
			} else {
				typeAndId = new TypeAndId(entityType, convertId(idAsString.substring(1), entityType));
			}
		} else {
			throw new IllegalArgumentException("cannotparse uri " + uri);
		}
		return typeAndId;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		try {
			TypeAndId typeAndId = parse(uri);
			Matcher matcher = singleResourcePattern.matcher(uri);
			try {
				if (typeAndId.getId() == null) {
					// it is a collection resource
					Paging paging = parsePaging(req);
					Sorting sorting = parseSorting(req, typeAndId.getEntityType());
					Query query = parseQuery(req, typeAndId.getEntityType());
					Result result = readEntities(typeAndId.getEntityType(), query, sorting, paging);
					int start = paging == null ? 0 : paging.getStart();
					resp.setHeader(CONTENT_RANGE_HEADER, "items " + start + "-" + result.entities.size() + "/"
							+ result.totalCount);
					objectMapper.writeValue(resp.getWriter(), result.entities);
				} else {
					Object entity = readEntity(typeAndId);
					if (entity == null) {
						// 404
						resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					} else {
						objectMapper.writeValue(resp.getWriter(), entity);
					}
				}

			} catch (Exception e) {
				handle500Error(resp, e);
			}
		} catch (Exception e) {
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

	}

	private Query parseQuery(HttpServletRequest req, EntityType<?> entityType) {
		Query query = null;
		String queryString = req.getParameter("query");
		if (StringUtils.isNotEmpty(queryString)) {
			try {
				ObjectNode node = (ObjectNode) objectMapper.readTree(queryString);
				JsonNode jsonNode = node.get("op");
				if (jsonNode != null && !jsonNode.isNull()) {
					List<AttributePredicate<?>> predicates = new ArrayList<AttributePredicate<?>>();
					boolean or = jsonNode.getTextValue().equals("or");
					ArrayNode predicateNodes = (ArrayNode) node.get("data");
					Iterator<JsonNode> iterator = predicateNodes.iterator();
					while (iterator.hasNext()) {
						ObjectNode next = (ObjectNode) iterator.next();
						Operator operator = parseOperator(next.get("op").getTextValue());
						ArrayNode operands = (ArrayNode) next.get("data");
						String attributeCode = ((ObjectNode) operands.get(0)).get("data").getTextValue();
						SingleAttribute<?> attribute = (SingleAttribute<?>) entityType.getAttribute(attributeCode);
						Object value = JsonUtils.convertToJava(((ObjectNode) operands.get(1)).get("data"));
						predicates.add(new AttributePredicate(attribute, operator, value));
					}
					query = new Query(or, predicates);
				}
			} catch (JsonProcessingException e) {
				throw new TechnicalException("cannot parse query", e);
			} catch (IOException e) {
				throw new TechnicalException("cannot parse query", e);
			}
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
		} else {
			throw new IllegalArgumentException("unknown operator" + op);
		}
	}

	protected boolean endswithEqual(String op) {
		return op.endsWith("Equal");
	}

	protected Paging parsePaging(HttpServletRequest req) {
		Paging paging = null;
		String contentRange = req.getHeader("Range");
		if (contentRange != null) {
			Matcher matcher = CONTENT_RANGE_PATTERN.matcher(contentRange);
			if (matcher.find()) {
				paging = new Paging(Integer.parseInt(matcher.group(1)), Integer.parseInt(matcher.group(2)));
			}
		}
		return paging;
	}

	protected Sorting parseSorting(HttpServletRequest req, EntityType<?> entityType) {
		Sorting sorting = null;
		String sortingValue = req.getParameter(sortParam);
		List<AttributeSorting> attributeSortings = new LinkedList<AttributeSorting>();
		if (sortingValue != null) {
			Matcher matcher = SORTING_PATTERN.matcher(sortingValue);
			while (matcher.find()) {
				String attributeCode = matcher.group(2);
				String dir = matcher.group(1);
				SingleAttribute<?> attribute = (SingleAttribute<?>) entityType.getAttribute(attributeCode);
				if (attribute == null) {
					throw new TechnicalException("cannot find sorting attribute " + attributeCode);
				}
				attributeSortings.add(new AttributeSorting(attribute, !dir.equals("-")));
			}
			sorting = new Sorting(attributeSortings);
		}
		return sorting;
	}

	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		try {
			String uri = req.getRequestURI();
			Matcher matcher = singleResourcePattern.matcher(uri);
			if (matcher.find()) {
				String type = matcher.group(1);
				String idAsString = matcher.group(2);
				BufferedReader reader = req.getReader();
				JsonNode jsonNode = objectMapper.readTree(reader);
				updateEntity(idAsString.substring(1), type, jsonNode);
			} else {
				// 404
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			handle500Error(resp, e);
		}

	}

	public String getCollectionUri(EntityType<?> entityType) {
		return uriPrefix + "/" + entityType.getCode();
	}

	public ConverterFactory getIdConverterfactory() {
		return idConverterfactory;
	}

	public String getUri(EntityType<?> type, String id) {
		return uriPrefix + "/" + type.getCode() + "/" + id;
	}

	@PostConstruct
	public void initialize() {
		metaType = entityTypeRepository.getEntityType(EntityType.class);
		derivedTypeAttribute = metaType.getMetaAttribute(DerivedObject.META_ATTRIBUTE_CODE);
		singleResourcePattern = Pattern.compile(uriPrefix + REST_PATTERN);
	}

	private Result readEntities(EntityType<?> entityType, Query query, Sorting sorting, Paging paging) {
		final DerivedType derivedType = (DerivedType) derivedTypeAttribute.getValue(entityType);
		EntityType<Object> originalType = (EntityType<Object>) derivedType.getOriginalType();
		FindByTypeService findByTypeService = originalType.getService(FindByTypeService.class);
		Object result = findByTypeService.getEntities(originalType, query, sorting, paging, new ListCallback<Object>() {

			@Override
			public Object process(List<Object> entities, long totalCount) {
				UniTransformation<Object, ObjectNode> ab = (UniTransformation<Object, ObjectNode>) derivedType
						.getTransformation().getAB();
				Result result = new Result();
				ArrayNode arrayNode = objectMapper.createArrayNode();
				for (Object entity : entities) {
					ObjectNode json = ab.convert(entity, new JacksonTransformationContext(entityTypeRepository));
					arrayNode.add(json);
				}
				result.entities = arrayNode;
				result.totalCount = totalCount;
				return result;
			}
		});
		return (Result) result;
	}

	protected Object readEntity(TypeAndId typeAndId) {
		EntityType<?> entityType = typeAndId.getEntityType();
		final DerivedType derivedType = (DerivedType) derivedTypeAttribute.getValue(entityType);
		EntityType<Object> originalType = (EntityType<Object>) derivedType.getOriginalType();
		FindByIdService findByIdService = originalType.getService(FindByIdService.class);

		Object json = findByIdService.findById(originalType, typeAndId.getId(), new SingleCallback<Object>() {

			@Override
			public Object process(Object entity) {
				UniTransformation<Object, ObjectNode> ab = (UniTransformation<Object, ObjectNode>) derivedType
						.getTransformation().getAB();
				return ab.convert(entity, new JacksonTransformationContext(entityTypeRepository));
			}
		});
		return json;
	}

	public void setIdConverterfactory(ConverterFactory idConverterfactory) {
		this.idConverterfactory = idConverterfactory;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	protected ObjectNode updateEntity(String idAsString, String type, final Object updatedObject) {
		EntityType<Object> entityType = entityTypeRepository.getEntityType(type);
		SimpleValidationContext context = new SimpleValidationContext(entityTypeRepository);
		ValidationService validationService = entityType.getService(ValidationService.class);
		if (validationService != null) {
			validationService.validate(entityType, context, updatedObject);
			if (context.getErrors().size() > 0) {
				ReturnErrorObject returnErrorObject = new ReturnErrorObject();
				returnErrorObject.setErrors(context.getErrors());
				return createErrorNode(returnErrorObject);
			}
		}
		final DerivedType derivedType = (DerivedType) derivedTypeAttribute.getValue(entityType);
		final EntityType<Object> originalType = (EntityType<Object>) derivedType.getOriginalType();
		Serializable id = convertId(idAsString, originalType);
		FindByIdService findByIdService = originalType.getService(FindByIdService.class);
		StatefulUpdateService crudService = originalType.getService(StatefulUpdateService.class);

		ReturnErrorObject returnErrorObject = crudService.update(id, originalType, new UpdateCallback() {

			@Override
			public ReturnErrorObject update(Object entity) {
				UniTransformation<Object, Object> ba = (UniTransformation<Object, Object>) derivedType
						.getTransformation().getBA();
				ba.merge(updatedObject, entity, new JacksonTransformationContext(entityTypeRepository));
				ValidationService validationService = originalType.getService(ValidationService.class);
				if (validationService != null) {
					SimpleValidationContext context = new SimpleValidationContext(entityTypeRepository);
					validationService.validate(originalType, context, entity);
					if (context.getErrors().size() > 0) {
						ReturnErrorObject returnErrorObject = new ReturnErrorObject();
						returnErrorObject.setErrors(context.getErrors());
						return returnErrorObject;
					}
				}
				return null;
			}
		});
		if (returnErrorObject == null) {
			return null;
		} else {
			return createErrorNode(returnErrorObject);
		}
	}

	public void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			String uri = req.getRequestURI();
			Matcher matcher = singleResourcePattern.matcher(uri);
			if (matcher.find()) {
				String type = matcher.group(1);
				BufferedReader reader = req.getReader();
				JsonNode jsonNode = objectMapper.readTree(reader);
				Object returnValue = createEntity(type, jsonNode);
				objectMapper.writeValue(resp.getWriter(), returnValue);
			} else {
				// 404
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			handle500Error(resp, e);
		}

	}

	private Object createEntity(String type, JsonNode entity) {
		EntityType<Object> entityType = entityTypeRepository.getEntityType(type);
		SimpleValidationContext context = new SimpleValidationContext(entityTypeRepository);
		ValidationService validationService = entityType.getService(ValidationService.class);
		if (validationService != null) {
			validationService.validate(entityType, context, entity);
			if (context.getErrors().size() > 0) {
				ReturnErrorObject returnErrorObject = new ReturnErrorObject();
				returnErrorObject.setErrors(context.getErrors());
				return createErrorNode(returnErrorObject);
			}
		}
		final DerivedType derivedType = (DerivedType) derivedTypeAttribute.getValue(entityType);
		final EntityType<Object> originalType = (EntityType<Object>) derivedType.getOriginalType();

		PersistenceService persistenceService = originalType.getService(PersistenceService.class);

		UniTransformation<JsonNode, Object> transformation = (UniTransformation<JsonNode, Object>) derivedType
				.getTransformation().getBA();
		// TODO insert should be ableto return validation errors
		Object transformedEntity = transformation.convert(entity,
				new JacksonTransformationContext(entityTypeRepository));

		Serializable id = persistenceService.insert(originalType, transformedEntity);

		return id;
	}

	public void doDelete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		try {
			String uri = req.getRequestURI();
			Matcher matcher = singleResourcePattern.matcher(uri);
			if (matcher.find()) {
				String type = matcher.group(1);

				String idAsString = matcher.group(2);
				if (idAsString == null) {
					// 404
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				} else {
					// TODO errors should be possible too
					deleteEntity(idAsString.substring(1), type);

				}
			} else {
				// 404
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			handle500Error(resp, e);
		}
	}

	protected void handle500Error(HttpServletResponse resp, Exception e) throws IOException {
		logger.error(e);
		resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		e.printStackTrace(resp.getWriter());
		resp.flushBuffer();
	}

	private void deleteEntity(String idAsString, String type) {
		EntityType<Object> entityType = entityTypeRepository.getEntityType(type);
		final DerivedType derivedType = (DerivedType) derivedTypeAttribute.getValue(entityType);
		final EntityType<Object> originalType = (EntityType<Object>) derivedType.getOriginalType();
		Serializable id = convertId(idAsString, originalType);
		DeletionService deletionService = originalType.getService(DeletionService.class);
		if (deletionService != null) {
			deletionService.delete(originalType, id);
		}
	}

	public String getUriById(EntityType<?> entityType, Serializable id) {
		return getUri(entityType, String.valueOf(id));
	}
}
