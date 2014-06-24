package org.atemsource.atem.service.gform.creator;

import javax.validation.constraints.Pattern;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.gform.TypeBuilder;
import org.atemsource.atem.service.meta.service.binding.ClassUtils;

public class PatternMixin 

implements  TypeCreatorMixins {

	@Override
	public void update(TypeBuilder builder, Attribute<?,?> attribute) {
		Pattern value = (Pattern) attribute.getMetaValue(ClassUtils.getMetaAttributePath(javax.validation.constraints.Pattern.class));
		if (value!=null) {
			builder.getNode().put("invalidMessage", value.message());
			builder.getNode().put("pattern", value.regexp());
		}
	}

	

}
