package org.atemsource.atem.service.meta.service.binding.editor;

import java.util.List;

import org.atemsource.atem.api.attribute.Attribute;

public class AttributeList {
private List<AttributeEditor> attributes;

public List<AttributeEditor> getAttributes() {
	return attributes;
}

public void setAttributes(List<AttributeEditor> attributes) {
	this.attributes = attributes;
}
}
