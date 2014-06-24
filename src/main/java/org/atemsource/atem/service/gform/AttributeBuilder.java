package org.atemsource.atem.service.gform;

import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;

public class AttributeBuilder extends TypeBuilder {

	public TypeBuilder element() {
		return new TypeBuilder(getObjectMapper(), addNode("element"));

	}

	public AttributeBuilder(ObjectMapper objectMapper, ObjectNode node) {
		super(objectMapper, node);
	}

	public GroupBuilder group() {
		return new GroupBuilder(addNode("group"), getObjectMapper());
	}

	public GroupsBuilder groups() {
		return new GroupsBuilder(getObjectMapper(), addArray("groups"));
	}

	public void code(String code) {
		getNode().put("code", code);
	}

	public void label(String label) {
		getNode().put("label", label);

	}

	public void description(String description) {
		getNode().put("description", description);
	}

	public void required() {
		getNode().put("required",true);
	}

}
