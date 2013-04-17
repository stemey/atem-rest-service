package org.atemsource.atem.service.meta.service.binding.dojocolumn;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.primitive.TextType;
import org.atemsource.atem.service.meta.service.binding.attributetype.AttributeTransformationCreator;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.constraint.DateFormat;
import org.atemsource.atem.utility.transform.api.constraint.PossibleValues;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Constant;

public class SelectMixin implements AttributeTransformationCreator {

	public EntityTypeTransformation<?, ?> addAttributeTransformation(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
		transformationBuilder.transformCustom(Constant.class).to("cellType").value(String.class, "dojox.grid.cells.Select");
		
		transformationBuilder.transform().to("dateformat").from("@" + DateFormat.META_ATTRIBUTE_CODE + ".pattern");
		
		transformationBuilder.transformCollection().to("options")
		.from("@" + PossibleValues.META_ATTRIBUTE_CODE + ".values").convertEmptyToNull();
		extend(transformationBuilder);
		return transformationBuilder.buildTypeTransformation();
	}
	
	@Override
	public boolean canTransform(Attribute<?,?> attribute) {
		return attribute.getTargetType().getJavaType()==String.class && attribute.getMetaValue("@" + PossibleValues.META_ATTRIBUTE_CODE + ".values")!=null;
	}

	protected void extend(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
	}


	@Override
	public String getTargetName() {
		return "select";
	}

}