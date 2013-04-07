package org.atemsource.atem.service.meta.service.binding.validation;

import javax.validation.constraints.NotNull;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.primitive.IntegerType;
import org.atemsource.atem.service.meta.service.binding.AttributeMixin;
import org.atemsource.atem.service.meta.service.binding.ClassUtils;
import org.atemsource.atem.service.meta.service.binding.attributetype.AttributeTransformationCreator;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Constant;
import org.atemsource.atem.utility.transform.impl.builder.Mixin;

public class RequiredMixin implements AttributeMixin {

	@Override
	public void mixin(TypeTransformationBuilder<?, ?> builder) {
		builder.transform().from(ClassUtils.getAttributeName(javax.validation.constraints.NotNull.class)).to("required").convert(new JavaUniConverter<NotNull,Boolean>() {

			@Override
			public Boolean convert(NotNull a, TransformationContext ctx) {
				return a!=null;
			}
		});
	}

	

}
