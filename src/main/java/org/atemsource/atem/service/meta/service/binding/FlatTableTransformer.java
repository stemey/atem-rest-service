package org.atemsource.atem.service.meta.service.binding;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.annotation.Cardinality;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.api.view.Visitor;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;


public class FlatTableTransformer
{
	private DynamicEntityTypeSubrepository<?> dynamicEntityTypeSubrepository;

	private TransformationBuilderFactory transformationBuilderFactory;

	public EntityTypeTransformation<?, ?> createTransformation(EntityType<?> originalType)
	{
		final EntityTypeBuilder targetBuilder =
			dynamicEntityTypeSubrepository.createBuilder("flattable:" + originalType.getCode());
		final TypeTransformationBuilder<?, ?> builder = transformationBuilderFactory.create(originalType, targetBuilder);
		String path = "";
		originalType.visit(new ViewVisitor<String>() {

			@Override
			public void visit(String context, Attribute attribute)
			{
				builder.transform().from(context + attribute.getCode()).to(attribute.getCode());
			}

			@Override
			public void visit(String context, Attribute attribute, Visitor<String> targetTypeVisitor)
			{
				if (attribute.getTargetCardinality() == Cardinality.ONE)
				{
					targetTypeVisitor.visit(context + attribute.getCode() + ".");
				}
			}

			@Override
			public boolean visitSubView(String context, View view)
			{
				return false;
			}

			@Override
			public boolean visitSuperView(String context, View view)
			{
				return true;
			}
		}, path);
		return builder.buildTypeTransformation();
	}
}
