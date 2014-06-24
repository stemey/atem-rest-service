package org.atemsource.atem.service.gform.creator;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.service.gform.AttributeBuilder;
import org.atemsource.atem.service.gform.TypeBuilder;
import org.atemsource.atem.utility.transform.api.constraint.PossibleValues;
import org.codehaus.jackson.node.ArrayNode;

public class PossibleValuesMixin implements TypeCreatorMixins{

	@Override
	public void update(TypeBuilder builder, Attribute<?,?> attribute) {
		PossibleValues metaValue = (PossibleValues) attribute.getMetaValue(PossibleValues.META_ATTRIBUTE_CODE);
		if (metaValue!=null) {
			ArrayNode array = builder.addArray("values");
			for (String value:metaValue.getValues()) {
				array.add(value);
			}
		}
	}

}
