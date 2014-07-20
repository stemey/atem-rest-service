package org.atemsource.atem.service.gform.creator;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.json.JsonEntityTypeImpl;
import org.atemsource.atem.service.gform.AttributeBuilder;
import org.atemsource.atem.service.gform.GformContext;

public class MultiEmbeddedAttributeCreator extends MultiTypeAttributeCreator {

	
	@Override
	public boolean handles(Attribute attribute) {
		boolean handles=false;
		if ( attribute.getTargetType() instanceof EntityType<?> && attribute instanceof SingleAttribute<?>) {
			EntityType<?> targetType=(EntityType<?>) attribute.getTargetType();
			handles = targetType.getSubEntityTypes().size()>0;
		}
		return handles;
	}

	@Override
	public void addMore(AttributeBuilder attributeBuilder, Attribute attribute) {
		JsonEntityTypeImpl targetType = (JsonEntityTypeImpl) attribute.getTargetType();
		attributeBuilder.type("object");
		attributeBuilder.getNode().put("typeProperty",targetType.getTypeProperty());
		createGroups(attributeBuilder, targetType);
	}

}
