package org.atemsource.atem.service.gform.creator;

import org.atemsource.atem.service.meta.service.binding.AttributeMixin;
import org.atemsource.atem.service.meta.service.binding.ClassUtils;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;

public class SizeMixin implements AttributeMixin {

	@Override
	public void mixin(TypeTransformationBuilder<?, ?> builder) {
		builder.transform().from(ClassUtils.getMetaAttributePath(javax.validation.constraints.Size.class)+".max").to("maxLength");
	}

	

}
