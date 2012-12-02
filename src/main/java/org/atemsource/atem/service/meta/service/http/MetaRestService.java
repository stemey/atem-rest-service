package org.atemsource.atem.service.meta.service.http;

import java.io.IOException;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.meta.service.model.Meta;
import org.atemsource.atem.service.meta.service.provider.MetaProvider;
import org.atemsource.atem.utility.binding.Binder;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class MetaRestService {
	@Inject
	private MetaProvider metaProvider;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private ObjectMapper objectMapper;

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public Binder getBinder() {
		return binder;
	}

	public void setBinder(Binder binder) {
		this.binder = binder;
	}

	private Binder binder;

	private ObjectNode json;

	@PostConstruct
	public void initialize() {
		json = createJson();
	}

	public void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
		ObjectNode json = createJson();
		objectMapper.writeValue(resp.getWriter(), json);
	}

	protected ObjectNode createJson() {
		Meta meta = metaProvider.getMeta();
		EntityType<Meta> entityType = entityTypeRepository.getEntityType(Meta.class);
		if (entityType==null) {
			throw new TechnicalException("Meta not available as entitType");
		}
		EntityTypeTransformation<Meta, Object> transformation = binder.getTransformation(Meta.class);
		Object json = transformation.getAB().convert(meta, new SimpleTransformationContext(entityTypeRepository));
		return (ObjectNode) json;
	}
}
