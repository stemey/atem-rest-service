package org.atemsource.atem.service.gform.creator;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.gform.AttributeBuilder;
import org.atemsource.atem.service.gform.GformContext;
import org.atemsource.atem.service.gform.GroupBuilder;
import org.atemsource.atem.service.gform.GroupsBuilder;

public class MultiEmbeddedArrayAttributeCreator extends MultiTypeAttributeCreator {

	@Override
	public boolean handles(Attribute attribute) {
		boolean handles = false;
		if (attribute.getTargetType() instanceof EntityType<?>
				&& attribute instanceof CollectionAttribute<?, ?>) {
			EntityType<?> targetType = (EntityType<?>) attribute
					.getTargetType();
			handles = targetType.getSubEntityTypes().size() > 0;
		}
		return handles;
	}

	@Override
	public void addMore(AttributeBuilder attributeBuilder, Attribute attribute) {
		EntityType<?> targetType = (EntityType<?>) attribute.getTargetType();
		attributeBuilder.type("array");
		createGroups(attributeBuilder, targetType);
	}

	

}
