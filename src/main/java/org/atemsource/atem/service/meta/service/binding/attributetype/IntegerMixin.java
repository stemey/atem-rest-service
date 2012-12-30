package org.atemsource.atem.service.meta.service.binding.attributetype;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.primitive.IntegerType;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Constant;
import org.atemsource.atem.utility.transform.impl.builder.Mixin;

public class IntegerMixin implements AttributeTransformationCreator {

	public EntityTypeTransformation<?, ?> addAttributeTransformation(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
		transformationBuilder.transformCustom(Constant.class).to("type").value(String.class, "number");
		transformationBuilder.transformCustom(Constant.class).to("fraction").value(Integer.class, 0);
		extend(transformationBuilder);
		return transformationBuilder.buildTypeTransformation();
	}
	
	public Class<?> getType() {
		return Integer.class;
	}

	protected void extend(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
	}



	

}
