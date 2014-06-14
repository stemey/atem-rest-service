package org.atemsource.atem.service.meta.service.binding.attributetype;

import java.awt.geom.Arc2D.Float;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.primitive.FloatType;
import org.atemsource.atem.api.type.primitive.IntegerType;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Constant;
import org.atemsource.atem.utility.transform.impl.builder.Mixin;

public class FloatMixin implements AttributeTransformationCreator {

	public EntityTypeTransformation<?, ?> addAttributeTransformation(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
		transformationBuilder.transformCustom(Constant.class).to("type").value(String.class, "number");
		extend(transformationBuilder);
		return transformationBuilder.buildTypeTransformation();
	}
	
	

	protected void extend(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
	}



	@Override
	public boolean canTransform(Attribute<?,?> attribute) {
		return attribute.getTargetType() instanceof FloatType;
	}

	
	@Override
	public String getTargetName() {
		return "float";
	}	

}
