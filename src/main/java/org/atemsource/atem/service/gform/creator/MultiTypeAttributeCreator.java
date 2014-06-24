package org.atemsource.atem.service.gform.creator;

import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.gform.AttributeBuilder;
import org.atemsource.atem.service.gform.AttributeCreator;
import org.atemsource.atem.service.gform.GformContext;
import org.atemsource.atem.service.gform.GroupBuilder;
import org.atemsource.atem.service.gform.GroupsBuilder;

public class MultiTypeAttributeCreator extends AttributeCreator {

	protected void createGroups(AttributeBuilder attributeBuilder,
			EntityType<?> targetType) {
		Set<EntityType> allSubEntityTypes = targetType.getAllSubEntityTypes();
		SortedSet<EntityType<?>> allConcreteSubTypes = new TreeSet<EntityType<?>>(
				getComparator());

		for (EntityType<?> type : allSubEntityTypes) {
			if (!type.isAbstractType()) {
				allConcreteSubTypes.add(type);
			}
		}
		GroupsBuilder groups = attributeBuilder.groups();
		for (EntityType<?> type : allConcreteSubTypes) {
			GroupBuilder groupBuilder = groups.add();
			groupBuilder.code(getCtx().getTypeCodeResolver().getTypeCode(type));
			getCtx().getGroupCreator(targetType).create(groupBuilder, type);
		}
	}

	protected Comparator<? super EntityType<?>> getComparator() {
		return new Comparator<EntityType<?>>() {

			@Override
			public int compare(EntityType<?> arg0, EntityType<?> arg1) {
				return arg0.getCode().compareTo(arg1.getCode());
			}
		};
	}
}