package org.atemsource.atem.service.gform.creator;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.gform.AttributeBuilder;
import org.atemsource.atem.service.gform.TypeBuilder;
import org.atemsource.atem.utility.transform.api.constraint.DateFormat;

public class DateFormatMixin implements TypeCreatorMixins{

	@Override
	public void update(TypeBuilder builder, Attribute<?, ?> attribute) {
		DateFormat metaValue = (DateFormat) attribute.getMetaValue(DateFormat.META_ATTRIBUTE_CODE);
		if (metaValue!=null) {
			builder.getNode().put("format",metaValue.getPattern());
			if (metaValue.getMessage()!=null) {
				builder.getNode().put("invalidMessage",metaValue.getMessage());
			}
		}
	}

}
