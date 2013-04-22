package org.atemsource.atem.service.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;
import java.util.Observer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.service.FindByIdService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.utility.transform.api.JacksonTransformationContext;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.validation.SimpleValidationContext;
import org.atemsource.atem.utility.validation.ValidationService;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class EntityRestService {

	@Inject
	private EntityTypeRepository entityTypeRepository;
	private EntityType<EntityType> metaType;

	@PostConstruct
	public void initialize() {
		metaType = entityTypeRepository.getEntityType(EntityType.class);
		derivedTypeAttribute = metaType.getMetaAttribute(DerivedObject.META_ATTRIBUTE_CODE);
		pattern = Pattern.compile(uriPrefix+"/([^/]+)/([^/]+)");
	}
	
	private String uriPrefix="/entity/entities";

	private Pattern pattern;
	
	public String getUri(EntityType<?> type,String id) {
		return uriPrefix+"/"+type.getCode()+"/"+id;
	}
	
	private Attribute derivedTypeAttribute;

	private ObjectMapper objectMapper;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		Matcher matcher = pattern.matcher(uri);
		if (matcher.find()) {
			String type = matcher.group(1);
			String idAsString = matcher.group(2);
			Object entity = readEntity(idAsString, type);
			if (entity == null) {
				// 404
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			} else {
				objectMapper.writeValue(resp.getWriter(), entity);
			}
		} else {
			// 404
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

	}

	public void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String uri = req.getRequestURI();
		Matcher matcher = pattern.matcher(uri);
		if (matcher.find()) {
			String type = matcher.group(1);
			String idAsString = matcher.group(2);
			BufferedReader reader = req.getReader();
			JsonNode jsonNode = objectMapper.readTree(reader);
			updateEntity(idAsString, type, jsonNode);
		} else {
			// 404
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	protected Object readEntity(String idAsString, String type) {
		EntityType<Object> entityType = entityTypeRepository.getEntityType(type);
		DerivedType derivedType = (DerivedType) derivedTypeAttribute.getValue(entityType);
		EntityType<?> originalType = derivedType.getOriginalType();
		FindByIdService findByIdService = originalType.getService(FindByIdService.class);
		Object entity = findByIdService.findById(originalType, idAsString);
		UniTransformation<Object, ObjectNode> ab = (UniTransformation<Object, ObjectNode>) derivedType
				.getTransformation().getAB();
		ObjectNode json = ab.convert(entity, new JacksonTransformationContext(entityTypeRepository));
		return json;
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
		FindByIdService findByIdService = originalType.getService(FindByIdService.class);
		StatefulUpdateService crudService = originalType.getService(StatefulUpdateService.class);
		final Object currentObject = findByIdService.findById(originalType, idAsString);
		ReturnErrorObject returnErrorObject = crudService.update(idAsString, originalType, new UpdateCallback() {

			@Override
			public ReturnErrorObject update(Object entity) {
				UniTransformation<Object, Object> ba = (UniTransformation<Object, Object>) derivedType
						.getTransformation().getBA();
				ba.merge(updatedObject, currentObject, new JacksonTransformationContext(entityTypeRepository));
				ValidationService validationService = originalType.getService(ValidationService.class);
				if (validationService != null) {
					SimpleValidationContext context = new SimpleValidationContext(entityTypeRepository);
					validationService.validate(originalType, context, currentObject);
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
}
