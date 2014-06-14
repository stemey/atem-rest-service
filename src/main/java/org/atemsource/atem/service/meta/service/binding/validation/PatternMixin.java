package org.atemsource.atem.service.meta.service.binding.validation;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.primitive.IntegerType;
import org.atemsource.atem.service.meta.service.binding.AttributeMixin;
import org.atemsource.atem.service.meta.service.binding.ClassUtils;
import org.atemsource.atem.service.meta.service.binding.attributetype.AttributeTransformationCreator;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Constant;
import org.atemsource.atem.utility.transform.impl.builder.Mixin;

public class PatternMixin implements AttributeMixin {

	@Override
	public void mixin(TypeTransformationBuilder<?, ?> builder) {
		builder.transform().from(ClassUtils.getMetaAttributePath(javax.validation.constraints.Pattern.class)+".regexp").to("pattern");
		builder.transform().from(ClassUtils.getMetaAttributePath(javax.validation.constraints.Pattern.class)+".message").to("invalidMessage");
	}

	

}
