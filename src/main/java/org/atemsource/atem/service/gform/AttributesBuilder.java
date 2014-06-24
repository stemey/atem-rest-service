package org.atemsource.atem.service.gform;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class AttributesBuilder {

	private ArrayNode arrayNode;
	private ObjectMapper mapper;
	
	public AttributesBuilder(ArrayNode arrayNode, ObjectMapper mapper) {
		super();
		this.arrayNode = arrayNode;
		this.mapper = mapper;
	}

	public AttributeBuilder add() {
		ObjectNode node = mapper.createObjectNode();
		arrayNode.add(node);
		return new AttributeBuilder(mapper,node);
	}

}
