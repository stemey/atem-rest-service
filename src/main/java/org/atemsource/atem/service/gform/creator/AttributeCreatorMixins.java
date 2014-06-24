package org.atemsource.atem.service.gform.creator;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.gform.AttributeBuilder;

public interface AttributeCreatorMixins {
	public void update(AttributeBuilder builder, Attribute<?,?> attribute);
}
