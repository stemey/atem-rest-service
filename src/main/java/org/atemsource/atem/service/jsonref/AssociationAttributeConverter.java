package org.atemsource.atem.service.jsonref;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.attribute.annotation.Association;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.Visitor;
import org.atemsource.atem.service.refresolver.RefResolver;
import org.atemsource.atem.utility.binding.AttributeConverter;
import org.atemsource.atem.utility.binding.TransformationContext;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.codehaus.jackson.map.ObjectMapper;

public class AssociationAttributeConverter implements AttributeConverter {

	private RefResolver refResolver;
	


	private ObjectMapper objectMapper;

	public Converter<?, ?> createConverter(TransformationContext context, Attribute attribute,
			Visitor<TransformationContext> attributeVisitor) {
		
		
		if (attribute.getTargetType() instanceof EntityType<?>) {
		EntityType<?> targetType = (EntityType<?>) attribute.getTargetType();
		context.cascade(targetType, attributeVisitor);
		EntityTypeTransformation<?, Object> transformation = context.getTransformation(targetType);
		if (attribute instanceof JavaMetaData) {
			Association annotation = ((JavaMetaData) attribute).getAnnotation(Association.class);
			if (annotation != null && !annotation.composition()) {
				EntityType<Object> typeB = (EntityType<Object>) transformation.getTypeB();
				return new JsonRefConverter(refResolver, objectMapper, typeB);
			}else if (!attribute.isComposition()) {
				EntityType<Object> typeB = (EntityType<Object>) transformation.getTypeB();
				return new JsonRefConverter( refResolver,objectMapper, typeB);
			}
		}
		}
		return null;
	}

	

	public void setRefResolver(RefResolver refResolver) {
		this.refResolver = refResolver;
	}



	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

}
