package org.atemsource.atem.service.meta.service.binding;

import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;

public class AttributeDescriptionMixin implements AttributeMixin{

	@Override
	public void mixin(TypeTransformationBuilder<?,?> builder) {
		builder.transform().from("code").to("label");
	}

}
