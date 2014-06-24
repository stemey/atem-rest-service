package org.atemsource.atem.service.gform;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class GroupsBuilder {
	public GroupsBuilder(ObjectMapper objectMapper, ArrayNode groupsNode) {
		super();
		this.objectMapper = objectMapper;
		this.groupsNode = groupsNode;
	}

	private ObjectMapper objectMapper;
	private ArrayNode groupsNode;

	public GroupBuilder add() {
	ObjectNode groupNode = objectMapper.createObjectNode();
	groupsNode.add(groupNode);
	return new GroupBuilder(groupNode, objectMapper);
}
}
