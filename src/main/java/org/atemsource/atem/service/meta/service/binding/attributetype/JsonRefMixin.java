package org.atemsource.atem.service.meta.service.binding.attributetype;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.entity.EntityRestService;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Constant;

public class JsonRefMixin implements AttributeTransformationCreator {
	

	public EntityTypeTransformation<?, ?> addAttributeTransformation(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
		transformationBuilder.transformCustom(Constant.class).to("type").value(String.class, "ref");
		transformationBuilder.transform().from("targetType.typeCode").to("schemaUrl").convert(new JavaUniConverter<String,String>() {

			@Override
			public String convert(String typeCode, TransformationContext ctx) {
				return ""+typeCode;
			}
		});
		transformationBuilder.transform().from("targetType.typeCode").to("url").convert(new JavaUniConverter<String,String>() {

			@Override
			public String convert(String typeCode, TransformationContext ctx) {
				return ""+typeCode;
			}
		});
		transformationBuilder.transform().from("targetType.typeCode").to("searchUrl").convert(new JavaUniConverter<String,String>() {

			@Override
			public String convert(String typeCode, TransformationContext ctx) {
				return ""+typeCode;
			}
		});
		transformationBuilder.transformCustom(Constant.class).to("searchProperty").value(String.class, "label");
		extend(transformationBuilder);
		return transformationBuilder.buildTypeTransformation();
	}

	protected void extend(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
	}

	@Override
	public boolean canTransform(Attribute<?,?> attribute) {
		return attribute.getTargetType().getCode().equals("json-ref");
	}

	@Override
	public String getTargetName() {
		return "ref";
	}	
}
