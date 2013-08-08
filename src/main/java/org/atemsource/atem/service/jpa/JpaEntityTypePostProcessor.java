package org.atemsource.atem.service.jpa;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.extension.EntityTypePostProcessor;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.identity.Identity;
import org.atemsource.atem.spi.EntityTypeCreationContext;

public class JpaEntityTypePostProcessor implements EntityTypePostProcessor {

	public void postProcessEntityType(EntityTypeCreationContext context, EntityType<?> entityType) {
		if (entityType.getJavaType().getAnnotation(Entity.class)!=null) {
			for (Attribute<?, ?> attribute : entityType.getAttributes()) {
				JavaMetaData javaAttribute = (JavaMetaData) attribute;
				Id idAnnotation = javaAttribute.getAnnotation(Id.class);
				if (idAnnotation != null) {
					Identity identity = new Identity();
					identity.setAttributeCode(attribute.getCode());
					identity.setAutoGenerated(javaAttribute.getAnnotation(GeneratedValue.class) != null);
					entityType.getMetaType().getMetaAttribute(Identity.META_ATTRIBUTE_CODE).setValue(entityType, identity);
				}
			}
		}
	}

}