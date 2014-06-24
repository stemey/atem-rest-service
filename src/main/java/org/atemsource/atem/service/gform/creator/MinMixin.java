package org.atemsource.atem.service.gform.creator;

import javax.validation.constraints.Min;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.gform.TypeBuilder;
import org.atemsource.atem.service.meta.service.binding.ClassUtils;

public class MinMixin implements  TypeCreatorMixins {

	@Override
	public void update(TypeBuilder builder, Attribute<?,?> attribute) {
		Min value = (Min) attribute.getMetaValue(ClassUtils.getMetaAttributePath(javax.validation.constraints.Min.class));
		if (value!=null) {
			builder.getNode().put("min", value.value());
		}
	}

}
