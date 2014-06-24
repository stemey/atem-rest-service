package org.atemsource.atem.service.gform;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class NodeBuilder {

	private ObjectMapper objectMapper;
	private ObjectNode node;

	public NodeBuilder(ObjectMapper objectMapper, ObjectNode node) {
		super();
		this.objectMapper = objectMapper;
		this.node = node;
	}

	public ObjectMapper getObjectMapper() {
		return objectMapper;
	}

	public NodeBuilder() {
		super();
	}

	public ObjectNode getNode() {
		return node;
	}
	
	public ObjectNode addNode(String name) {
		ObjectNode newNode = objectMapper.createObjectNode();
		node.put(name,newNode);
		return newNode;
	}
	public ArrayNode addArray(String name) {
		ArrayNode newNode = objectMapper.createArrayNode();
		node.put(name,newNode);
		return newNode;
	}

}