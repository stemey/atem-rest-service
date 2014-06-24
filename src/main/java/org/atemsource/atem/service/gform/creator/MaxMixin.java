package org.atemsource.atem.service.gform.creator;

import javax.validation.constraints.Max;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.gform.TypeBuilder;
import org.atemsource.atem.service.meta.service.binding.ClassUtils;

public class MaxMixin implements TypeCreatorMixins {

	@Override
	public void update(TypeBuilder builder, Attribute<?,?> attribute) {
		Max value = (Max) attribute.getMetaValue(ClassUtils.getMetaAttributePath(javax.validation.constraints.Max.class));
		if (value!=null) {
			builder.getNode().put("max", value.value());
		}
	}

	

}
