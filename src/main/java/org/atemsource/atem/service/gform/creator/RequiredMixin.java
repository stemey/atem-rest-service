package org.atemsource.atem.service.gform.creator;

import javax.validation.constraints.NotNull;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.gform.AttributeBuilder;
import org.atemsource.atem.service.meta.service.binding.ClassUtils;

public class RequiredMixin implements AttributeCreatorMixins {

	@Override
	public void update(AttributeBuilder builder, Attribute<?, ?> attribute) {
		if (attribute.isRequired()) {
			builder.required();
		}else{
			Attribute metaAttribute = attribute.getMetaAttribute(ClassUtils.getMetaAttributeName(NotNull.class));
			if (metaAttribute!=null && metaAttribute.getValue(attribute)!=null) {
				builder.required();
			}
		}
	}	

}
