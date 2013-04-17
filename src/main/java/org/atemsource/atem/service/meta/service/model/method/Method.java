package org.atemsource.atem.service.meta.service.model.method;

import java.util.List;

import org.atemsource.atem.api.attribute.annotation.Association;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.service.meta.service.binding.EditorConversion;
import org.atemsource.atem.service.meta.service.model.Service;
import org.atemsource.atem.utility.transform.api.annotation.Conversion;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.codehaus.jackson.map.ObjectMapper;

public class Method extends Service {

	@Conversion(EditorConversion.class)
	private EntityType<?> params;

	public EntityType<?> getParams() {
		return params;
	}

	public void setParams(EntityType<?> params) {
		this.params = params;
	}

	public EntityType<?> getPathVariables() {
		return pathVariables;
	}

	public void setPathVariables(EntityType<?> pathVariables) {
		this.pathVariables = pathVariables;
	}

	@Conversion(EditorConversion.class)
	private EntityType<?> pathVariables;

	@Conversion(EditorConversion.class)
	private EntityType<?> requestBody;

	@JsonIgnore
	private Type<?> returnType;

	private String uriPattern;

	private String verb;

	public Method() {
	}

	public EntityType<?> getRequestBody() {
		return requestBody;
	}

	public Type<?> getReturnType() {
		return returnType;
	}

	public String getUriPattern() {
		return uriPattern;
	}

	public String getVerb() {
		return verb;
	}

	public void setRequestBody(EntityType<?> requestBody) {
		this.requestBody = requestBody;
	}

	public void setReturnType(Type<?> returnType) {
		this.returnType = returnType;
	}

	public void setUriPattern(String uriPattern) {
		this.uriPattern = uriPattern;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}

	@Override
	public String toString() {
		String result = null;
		try {
			result = new ObjectMapper().writeValueAsString(this);
		} catch (Exception e) {
			// ignore
		}
		return result;
	}

}
