package org.atemsource.atem.service.meta.service.binding.attributetype;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.entity.EntityRestService;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Constant;

public class JsonRefMixin implements AttributeTransformationCreator {
	
	private EntityTypeToUrlConverter entityTypeToUrlConverter;

	public EntityTypeTransformation<?, ?> addAttributeTransformation(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
		transformationBuilder.transformCustom(Constant.class).to("type").value(String.class, "ref");
		transformationBuilder.transform().from("targetType").to("url").convert(entityTypeToUrlConverter);
		extend(transformationBuilder);
		return transformationBuilder.buildTypeTransformation();
	}

	public void setEntityTypeToUrlConverter(EntityTypeToUrlConverter enityTypeToUrlConverter) {
		this.entityTypeToUrlConverter = enityTypeToUrlConverter;
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
