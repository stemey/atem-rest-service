package org.atemsource.atem.service.entity;

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
import org.atemsource.atem.api.service.FindByTypedIdService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class EntityRestService {

	@Inject
	private EntityTypeRepository entityTypeRepository;
	private EntityType<EntityType> metaType;

	@PostConstruct
	public void initialize() {
		metaType = entityTypeRepository.getEntityType(EntityType.class);
		derivedTypeAttribute = metaType.getMetaAttribute(DerivedObject.META_ATTRIBUTE_CODE);
	}

	private final Pattern pattern = Pattern.compile("/entities/([^/]+)/([^/]+)");
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

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	protected Object readEntity(String idAsString, String type) {
		EntityType<Object> entityType = entityTypeRepository.getEntityType(type);
		DerivedType derivedType = (DerivedType) derivedTypeAttribute.getValue(entityType);
		EntityType<?> originalType = derivedType.getOriginalType();
		CrudService crudService = originalType.getService(CrudService.class);
		Object entity = crudService.findEntity(originalType, idAsString);
		UniTransformation<Object, ObjectNode> ab = (UniTransformation<Object, ObjectNode>) derivedType
				.getTransformation().getAB();
		ObjectNode json = ab.convert(entity, new SimpleTransformationContext(entityTypeRepository));
		return json;
	}
}
