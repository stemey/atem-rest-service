package org.atemsource.atem.service.entity;

import java.io.IOException;
import java.io.Serializable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

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
		derivedTypeAttribute = metaType.getAttribute(DerivedObject.META_ATTRIBUTE_CODE);
	}

	private final Pattern pattern = Pattern.compile("/entities/(type)/(id)");
	private Attribute derivedTypeAttribute;

	private ObjectMapper objectMapper;

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String servletPath = req.getServletPath();
		Matcher matcher = pattern.matcher(servletPath);
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

	protected Object readEntity(String idAsString, String type) {
		EntityType<Object> entityType = entityTypeRepository.getEntityType(type);
		DerivedType derivedType = (DerivedType) derivedTypeAttribute.getValue(entityType);
		FindByTypedIdService findByIdService = entityType.getService(FindByTypedIdService.class);
		ExternalizableIdentityService identityService = entityType.getService(ExternalizableIdentityService.class);
		Serializable id = identityService.getIdFromString(entityType, idAsString);
		Object entity = findByIdService.findByTypedId(entityType, id);
		UniTransformation<Object, ObjectNode> ab = (UniTransformation<Object, ObjectNode>) derivedType
				.getTransformation().getAB();
		ObjectNode json = ab.convert(entity, new SimpleTransformationContext(entityTypeRepository));
		return json;
	}
}
