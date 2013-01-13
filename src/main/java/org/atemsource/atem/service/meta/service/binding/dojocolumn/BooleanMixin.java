package org.atemsource.atem.service.meta.service.binding.dojocolumn;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.api.type.primitive.BooleanType;
import org.atemsource.atem.api.type.primitive.IntegerType;
import org.atemsource.atem.service.meta.service.binding.attributetype.AttributeTransformationCreator;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Constant;
import org.atemsource.atem.utility.transform.impl.builder.Mixin;

public class BooleanMixin implements AttributeTransformationCreator {

	public EntityTypeTransformation<?, ?> addAttributeTransformation(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
		transformationBuilder.transformCustom(Constant.class).to("cellType").value(String.class, "dojox.grid.cells.Bool");
		extend(transformationBuilder);
		return transformationBuilder.buildTypeTransformation();
	}
	
	@Override
	public boolean canTransform(Attribute<?,?> attribute) {
		return attribute.getTargetType().getJavaType()==Boolean.class;
	}

	protected void extend(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
	}

	
	@Override
	public String getTargetName() {
		return "boolean";
	}
	

}
