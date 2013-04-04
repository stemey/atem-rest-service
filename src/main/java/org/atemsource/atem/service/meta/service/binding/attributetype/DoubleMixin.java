package org.atemsource.atem.service.meta.service.binding.attributetype;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Constant;

public class DoubleMixin implements AttributeTransformationCreator {

	public EntityTypeTransformation<?, ?> addAttributeTransformation(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
		transformationBuilder.transformCustom(Constant.class).to("type").value(String.class, "number");
		extend(transformationBuilder);
		return transformationBuilder.buildTypeTransformation();
	}

	protected void extend(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
	}

	@Override
	public boolean canTransform(Attribute<?,?> attribute) {
		return attribute.getTargetType().getJavaType()==Double.class;
	}

	@Override
	public String getTargetName() {
		return "double";
	}	
}
