package org.atemsource.atem.service.meta.service.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.service.meta.service.Cors;
import org.atemsource.atem.service.meta.service.provider.Category;
import org.atemsource.atem.service.meta.service.provider.MetaProvider;
import org.atemsource.atem.service.meta.service.provider.resource.SchemaRefResolver;
import org.atemsource.atem.utility.binding.Binder;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class MetaRestService {
	private MetaProvider metaProvider;
	
	private Cors cors = new Cors();

	private SchemaRefResolver schemaRefResolver;

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

	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		cors.appendCors(resp);
		if (schemaRefResolver.isResourceListing(req.getRequestURI())) {
			getResourceListing(req, resp);
		} else {
			EntityType<Object> schema = schemaRefResolver.parseSingleSchema(req
					.getRequestURI());
			if (schema != null) {
				getSchema(schema, req, resp);
			}

		}

	}

	public MetaProvider getMetaProvider() {
		return metaProvider;
	}

	public void setMetaProvider(MetaProvider metaProvider) {
		this.metaProvider = metaProvider;
	}

	public SchemaRefResolver getSchemaRefResolver() {
		return schemaRefResolver;
	}

	public void setSchemaRefResolver(SchemaRefResolver schemaRefResolver) {
		this.schemaRefResolver = schemaRefResolver;
	}

	private void getSchema(EntityType<Object> entityType,
			HttpServletRequest req, HttpServletResponse resp)
			throws ServletException {
		addJsonResponseHeader(resp);
		DerivedType derivedType = (DerivedType) entityType.getMetaType().getMetaAttribute(DerivedObject.META_ATTRIBUTE_CODE).getValue(entityType);
		
		ObjectNode gformSchema = metaProvider.getGformSchema(derivedType.getOriginalType());
		try {
			objectMapper.writeValue(resp.getWriter(), gformSchema);
		} catch (Exception e) {
			throw new ServletException("cannot render schema", e);
		}

	}

	private void getResourceListing(HttpServletRequest req,
			HttpServletResponse resp) throws ServletException {
		String url=req.getRequestURL().toString();
		String basePath=schemaRefResolver.getBasePath(url);
		Category category = metaProvider.getCategory(url);
		addJsonResponseHeader(resp);
		try {
			objectMapper.writeValue(resp.getWriter(), category);
		} catch (Exception e) {
			throw new ServletException("cannot render resource listing", e);
		}

	}

	private void addJsonResponseHeader(HttpServletResponse resp) {
		resp.setCharacterEncoding("UTF-8");
		resp.setContentType("application/json");
	}

	

}
