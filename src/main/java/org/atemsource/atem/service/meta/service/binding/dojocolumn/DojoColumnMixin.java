package org.atemsource.atem.service.meta.service.binding.dojocolumn;

import org.atemsource.atem.service.meta.service.binding.AttributeMixin;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;

public class DojoColumnMixin implements AttributeMixin{

	@Override
	public void mixin(TypeTransformationBuilder<?, ?> builder) {
		builder.transform().from("code").to("name");
		builder.transform().from("@DojoColumn.width").to("width");
		builder.transform().from("@DojoColumn.formatter").to("formatter");
		builder.transform().from("@DojoColumn.hidden").to("hidden");
	}

}
