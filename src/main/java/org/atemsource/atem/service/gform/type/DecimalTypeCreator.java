package org.atemsource.atem.service.gform.type;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.gform.TypeBuilder;
import org.atemsource.atem.service.gform.TypeCreator;

public class DecimalTypeCreator extends TypeCreator {

	@Override
	public void create(TypeBuilder typeBuilder, Attribute<?, ?> attribute) {
		typeBuilder.type("number");
		super.create(typeBuilder, attribute);
	}
	
	
	@Override
	public boolean handles(Attribute<?, ?> attribute) {
		Class<?> javaType = attribute.getTargetType().getJavaType();
		return Number.class.isAssignableFrom(javaType) || javaType==float.class || javaType==double.class;
	}

}
