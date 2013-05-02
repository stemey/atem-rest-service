package org.atemsource.atem.service.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.service.IdentityService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.ConverterFactory;
import org.atemsource.atem.utility.transform.api.JacksonTransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.validation.SimpleValidationContext;
import org.atemsource.atem.utility.validation.ValidationService;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;


public class EntityRestService
{

	public static final String REST_PATTERN = "/([^/]+)(/([^/]+))?";

	private Attribute derivedTypeAttribute;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private ConverterFactory idConverterfactory;

	private EntityType<EntityType> metaType;

	private ObjectMapper objectMapper;

	private Pattern singleResourcePattern;

	private final String uriPrefix = "/entity/entities";

	private Serializable convertId(String idAsString, EntityType<?> targetType)
	{
		// TODO we need to use the type of the transformed attribute
		IdentityService identityService = targetType.getService(IdentityService.class);
		Converter<String, Serializable> converter =
			(Converter<String, Serializable>) idConverterfactory.getConverter(String.class,
				identityService.getIdType(targetType).getJavaType());
		Serializable convertedId = converter.getAB().convert(idAsString, null);
		return convertedId;
	}

	ObjectNode createErrorNode(ReturnErrorObject returnErrorObject)
	{
		ObjectNode error = objectMapper.createObjectNode();
		ArrayNode errors = objectMapper.createArrayNode();
		error.put("errors", errors);
		for (org.atemsource.atem.utility.validation.AbstractValidationContext.Error e : returnErrorObject.getErrors())
		{
			ObjectNode singleError = objectMapper.createObjectNode();
			singleError.put("path", e.getPath());
			singleError.put("message", e.getMessage());
			errors.add(singleError);
		}
		return error;
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String uri = req.getRequestURI();
		Matcher matcher = singleResourcePattern.matcher(uri);
		if (matcher.find())
		{
			String type = matcher.group(1);

			String idAsString = matcher.group(2);
			if (idAsString == null)
			{
				// it is a collection resource
				ArrayNode entities = readEntities(type);
				objectMapper.writeValue(resp.getWriter(), entities);
			}
			else
			{
				Object entity = readEntity(idAsString, type);
				if (entity == null)
				{
					// 404
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				}
				else
				{
					objectMapper.writeValue(resp.getWriter(), entity);
				}
			}
		}
		else
		{
			// 404
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

	}

	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{
		String uri = req.getRequestURI();
		Matcher matcher = singleResourcePattern.matcher(uri);
		if (matcher.find())
		{
			String type = matcher.group(1);
			String idAsString = matcher.group(2);
			BufferedReader reader = req.getReader();
			JsonNode jsonNode = objectMapper.readTree(reader);
			updateEntity(idAsString, type, jsonNode);
		}
		else
		{
			// 404
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

	}

	public String getCollectionUri(EntityType<?> entityType)
	{
		return uriPrefix + "/" + entityType.getCode();
	}

	public ConverterFactory getIdConverterfactory()
	{
		return idConverterfactory;
	}

	public String getUri(EntityType<?> type, String id)
	{
		return uriPrefix + "/" + type.getCode() + "/" + id;
	}

	@PostConstruct
	public void initialize()
	{
		metaType = entityTypeRepository.getEntityType(EntityType.class);
		derivedTypeAttribute = metaType.getMetaAttribute(DerivedObject.META_ATTRIBUTE_CODE);
		singleResourcePattern = Pattern.compile(uriPrefix + REST_PATTERN);
	}

	private ArrayNode readEntities(String type)
	{
		EntityType<Object> entityType = entityTypeRepository.getEntityType(type);
		final DerivedType derivedType = (DerivedType) derivedTypeAttribute.getValue(entityType);
		EntityType<Object> originalType = (EntityType<Object>) derivedType.getOriginalType();
		FindByTypeService findByTypeService = originalType.getService(FindByTypeService.class);
		Object result = findByTypeService.getEntities(originalType, new ListCallback<Object>() {

			@Override
			public Object process(List<Object> entities)
			{
				UniTransformation<Object, ObjectNode> ab =
					(UniTransformation<Object, ObjectNode>) derivedType.getTransformation().getAB();
				ArrayNode arrayNode = objectMapper.createArrayNode();
				for (Object entity : entities)
				{
					ObjectNode json = ab.convert(entity, new JacksonTransformationContext(entityTypeRepository));
					arrayNode.add(json);
				}
				return arrayNode;
			}
		});
		return (ArrayNode) result;
	}

	protected Object readEntity(String idAsString, String type)
	{
		EntityType<Object> entityType = entityTypeRepository.getEntityType(type);
		final DerivedType derivedType = (DerivedType) derivedTypeAttribute.getValue(entityType);
		EntityType<Object> originalType = (EntityType<Object>) derivedType.getOriginalType();
		FindByIdService findByIdService = originalType.getService(FindByIdService.class);
		Serializable convertedId = convertId(idAsString, originalType);
		Object json = findByIdService.findById(originalType, convertedId, new SingleCallback<Object>() {

			@Override
			public Object process(Object entity)
			{
				UniTransformation<Object, ObjectNode> ab =
					(UniTransformation<Object, ObjectNode>) derivedType.getTransformation().getAB();
				return ab.convert(entity, new JacksonTransformationContext(entityTypeRepository));
			}
		});
		return json;
	}

	public void setIdConverterfactory(ConverterFactory idConverterfactory)
	{
		this.idConverterfactory = idConverterfactory;
	}

	public void setObjectMapper(ObjectMapper objectMapper)
	{
		this.objectMapper = objectMapper;
	}

	protected ObjectNode updateEntity(String idAsString, String type, final Object updatedObject)
	{
		EntityType<Object> entityType = entityTypeRepository.getEntityType(type);
		Serializable id = convertId(idAsString, entityType);
		SimpleValidationContext context = new SimpleValidationContext(entityTypeRepository);
		ValidationService validationService = entityType.getService(ValidationService.class);
		if (validationService != null)
		{
			validationService.validate(entityType, context, updatedObject);
			if (context.getErrors().size() > 0)
			{
				ReturnErrorObject returnErrorObject = new ReturnErrorObject();
				returnErrorObject.setErrors(context.getErrors());
				return createErrorNode(returnErrorObject);
			}
		}
		final DerivedType derivedType = (DerivedType) derivedTypeAttribute.getValue(entityType);
		final EntityType<Object> originalType = (EntityType<Object>) derivedType.getOriginalType();
		FindByIdService findByIdService = originalType.getService(FindByIdService.class);
		StatefulUpdateService crudService = originalType.getService(StatefulUpdateService.class);

		ReturnErrorObject returnErrorObject = crudService.update(id, originalType, new UpdateCallback() {

			@Override
			public ReturnErrorObject update(Object entity)
			{
				UniTransformation<Object, Object> ba =
					(UniTransformation<Object, Object>) derivedType.getTransformation().getBA();
				ba.merge(updatedObject, entity, new JacksonTransformationContext(entityTypeRepository));
				ValidationService validationService = originalType.getService(ValidationService.class);
				if (validationService != null)
				{
					SimpleValidationContext context = new SimpleValidationContext(entityTypeRepository);
					validationService.validate(originalType, context, entity);
					if (context.getErrors().size() > 0)
					{
						ReturnErrorObject returnErrorObject = new ReturnErrorObject();
						returnErrorObject.setErrors(context.getErrors());
						return returnErrorObject;
					}
				}
				return null;
			}
		});
		if (returnErrorObject == null)
		{
			return null;
		}
		else
		{
			return createErrorNode(returnErrorObject);
		}
	}
}
