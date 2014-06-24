package org.atemsource.atem.service.gform.creator;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.OrderableCollection;
import org.atemsource.atem.api.type.PrimitiveType;
import org.atemsource.atem.service.gform.AttributeBuilder;
import org.atemsource.atem.service.gform.AttributeCreator;
import org.atemsource.atem.service.gform.GformContext;
import org.atemsource.atem.service.gform.TypeBuilder;

public class PrimitiveListAttributeCreator extends AttributeCreator{
	

	
	@Override
	protected void addMore(AttributeBuilder attributeBuilder,
			Attribute attribute) {
		attributeBuilder.type("array");
		if (attribute.getTargetType() instanceof OrderableCollection) {
			attributeBuilder.getNode().put("reorderable",true);
		}
		getCtx().getTypeCreator(attribute).create(attributeBuilder.element(), attribute);
	}

	@Override
	public boolean handles(Attribute attribute) {
		return attribute instanceof CollectionAttribute && attribute.getTargetType() instanceof PrimitiveType;
	}


	
}
