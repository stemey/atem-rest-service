package org.atemsource.atem.service.gform.creator;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.PrimitiveType;
import org.atemsource.atem.service.gform.AttributeBuilder;
import org.atemsource.atem.service.gform.AttributeCreator;
import org.atemsource.atem.service.gform.TypeCreator;

public class PrimitiveAttributeCreator extends AttributeCreator{
	


	@Override
	protected void addMore(AttributeBuilder attributeBuilder,
			Attribute attribute) {
		TypeCreator typeCreator = getCtx().getTypeCreator(attribute);
			typeCreator
			.create(attributeBuilder,attribute);
	}

	
	@Override
	public boolean handles(Attribute attribute) {
		return attribute.getTargetType() instanceof PrimitiveType;
	}


	


	
}
