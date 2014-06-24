package org.atemsource.atem.service.gform;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class TypeBuilder extends NodeBuilder{
	public TypeBuilder(ObjectMapper objectMapper, ObjectNode node) {
		super(objectMapper, node);
	}

	public TypeBuilder type(String code) {
		getNode().put("type", code);
		return this;
	}

}
