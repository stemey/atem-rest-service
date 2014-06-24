package org.atemsource.atem.service.gform;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class GroupBuilder {

	private ObjectNode groupNode;
	private ObjectMapper objectMapper;

	public GroupBuilder(ObjectNode groupNode, ObjectMapper objectMapper) {
		super();
		this.groupNode = groupNode;
		this.objectMapper = objectMapper;
	}

	public void editor(String editor) {
		groupNode.put("editor", editor);
	}

	public AttributesBuilder attributes() {
		ArrayNode arrayNode = objectMapper.createArrayNode();
		groupNode.put("attributes", arrayNode);
		return new AttributesBuilder(arrayNode, objectMapper);
	}

	public GroupsBuilder groups() {
		ArrayNode arrayNode = objectMapper.createArrayNode();
		groupNode.put("groups", arrayNode);
		return new GroupsBuilder( objectMapper, arrayNode);
	}

	public void code(String typeCode) {
		groupNode.put("code",typeCode);
	}

}
