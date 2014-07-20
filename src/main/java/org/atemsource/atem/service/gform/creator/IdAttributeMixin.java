package org.atemsource.atem.service.gform.creator;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.service.IdentityAttributeService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.service.gform.AttributeBuilder;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;

public class IdAttributeMixin implements AttributeCreatorMixins {

	@Override
	public void update(AttributeBuilder builder, Attribute<?, ?> attribute) {
		EntityType<?> jsonType = attribute.getEntityType();
		Attribute metaAttribute = jsonType.getMetaType().getMetaAttribute(
				DerivedObject.META_ATTRIBUTE_CODE);
		if (metaAttribute != null) {
			DerivedType derivedType = (DerivedType) metaAttribute
					.getValue(jsonType);

			if (derivedType != null) {
				IdentityAttributeService identityAttributeService = (IdentityAttributeService) derivedType
						.getOriginalType().getService(
								IdentityAttributeService.class);
				if (identityAttributeService != null) {
					SingleAttribute<?> idAttribute = identityAttributeService
							.getIdAttribute(attribute.getEntityType());
					if (idAttribute != null && idAttribute==attribute) {
						Attribute derivedIdAttribute = derivedType
								.findDerived(idAttribute);
						if (derivedIdAttribute != null) {
							builder.getNode().put("disabled", true);
							builder.getNode().put("required", false);
						}
					}
				}

			}
		}
	}

}
