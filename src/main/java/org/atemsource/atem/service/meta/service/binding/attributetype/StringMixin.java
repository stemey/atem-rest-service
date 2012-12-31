package org.atemsource.atem.service.meta.service.binding.attributetype;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.primitive.TextType;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.constraint.DateFormat;
import org.atemsource.atem.utility.transform.api.constraint.PossibleValues;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Constant;

public class StringMixin implements AttributeTransformationCreator {

	public EntityTypeTransformation<?, ?> addAttributeTransformation(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
		transformationBuilder.transformCustom(Constant.class).to("type").value(String.class, "string");
		transformationBuilder.transform().to("dateformat").from("@" + DateFormat.META_ATTRIBUTE_CODE + ".pattern");
		transformationBuilder.transformCollection().to("values")
		.from("@" + PossibleValues.META_ATTRIBUTE_CODE + ".values").convertEmptyToNull();
		extend(transformationBuilder);
		return transformationBuilder.buildTypeTransformation();
	}
	
	public Class<?> getType() {
		return String.class;
	}

	protected void extend(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
	}


	

}
