package org.atemsource.atem.service.meta.service.binding.springrest;

import org.atemsource.atem.service.meta.service.binding.AttributeMixin;
import org.atemsource.atem.service.meta.service.binding.ClassUtils;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.springframework.web.bind.annotation.RequestParam;

public class RequestParamMixin implements AttributeMixin {

	@Override
	public void mixin(TypeTransformationBuilder<?,?> builder) {
		builder.transform().from(ClassUtils.getMetaAttributePath(RequestParam.class)+".required").to("required"); 
	}

	

}
