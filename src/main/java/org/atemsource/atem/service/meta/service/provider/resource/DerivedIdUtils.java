package org.atemsource.atem.service.meta.service.provider.resource;

import java.io.Serializable;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.utility.transform.api.meta.DerivedAttribute;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;


public class DerivedIdUtils
{
	public static String findDerivedIdProperty(EntityTypeTransformation<?, ?> transformation,
		SingleAttribute<? extends Serializable> idAttribute)
	{

		String originalIdCode = idAttribute.getCode();
		String derivedIdProperty = null;
		for (Attribute<?, ?> attribute : transformation.getEntityTypeB().getAttributes())
		{
			SingleAttribute<DerivedAttribute> derivedAttribute =
				(SingleAttribute<DerivedAttribute>) attribute.getMetaAttribute(DerivedAttribute.META_ATTRIBUTE_CODE);
			Attribute<?, ?> originalAttribute = derivedAttribute.getValue(attribute).getOriginalAttribute();
			if (originalAttribute.getCode().equals(originalIdCode))
			{
				derivedIdProperty = attribute.getCode();
				break;
			}
		}
		return derivedIdProperty;
	}
}
