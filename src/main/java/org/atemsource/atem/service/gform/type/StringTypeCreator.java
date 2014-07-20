package org.atemsource.atem.service.gform.type;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.gform.TypeBuilder;
import org.atemsource.atem.service.gform.TypeCreator;

public class StringTypeCreator extends TypeCreator{

	@Override
	public void create(TypeBuilder typeBuilder, Attribute<?, ?> attribute) {
		typeBuilder.type("string");
		super.create(typeBuilder, attribute);
	}

	@Override
	public boolean handles(Attribute<?, ?> attribute) {
		return attribute.getTargetType().getJavaType()  == String.class;
	}

}
