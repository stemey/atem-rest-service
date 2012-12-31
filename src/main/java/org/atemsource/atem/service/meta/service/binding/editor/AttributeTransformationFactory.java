package org.atemsource.atem.service.meta.service.binding.editor;


import javax.annotation.PostConstruct;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.primitive.NumberType;
import org.atemsource.atem.service.meta.service.annotation.ValidTypes;
import org.atemsource.atem.service.meta.service.binding.AbstractAttributeTransformationFactory;
import org.atemsource.atem.utility.transform.api.JavaTransformation;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.GenericTransformationBuilder;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;


public class AttributeTransformationFactory extends AbstractAttributeTransformationFactory {

	protected void transformComplexAttribute(EntityTypeTransformation complexTypeTransformation,
			TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
		transformValidTypes(transformationBuilder, complexTypeTransformation, complexTypeTransformation.getEntityTypeB());
		transformationBuilder.transform().from("targetType.code").to("type");
	}
	
	

	@SuppressWarnings("unchecked")
	private void transformValidTypes(TypeTransformationBuilder<Attribute, ?> attributeTransformationBuilder,
			final EntityTypeTransformation<EntityType, ObjectNode> typeTransformation, EntityType schemaType) {
		attributeTransformationBuilder.transformCustom(GenericTransformationBuilder.class)
				.transform(new JavaTransformation<Attribute<?, ?>, ObjectNode>() {

					@Override
					public void mergeAB(Attribute<?, ?> a, ObjectNode b, TransformationContext ctx) {
						// logger.debug("transforming " +
						// a.getEntityType().getEntityClass().getSimpleName() +
						// "."
						// + a.getCode());

						if (a.getTargetType() instanceof EntityType) {

							ValidTypes validTypes = ((JavaMetaData) a).getAnnotation(ValidTypes.class);

							ArrayNode validTypesArray = b.arrayNode();
							if (validTypes != null && validTypes.value().length == 0) {
								b.putNull("validTypes");
							} else if (validTypes != null && validTypes.value().length > 0) {
								for (Class clazz : validTypes.value()) {
									EntityType type = entityTypeRepository.getEntityType(clazz);
									UniTransformation<EntityType, ObjectNode> ab = typeTransformation.getAB();
									ObjectNode value = ab.convert(type, ctx);
									validTypesArray.add(value);
								}
								b.put("validTypes", validTypesArray);
							} else {
								// TODO the valid types should contain all
								// attributes in the subtypes.
								for (EntityType entityType : ((EntityType<?>) a.getTargetType())
										.getSelfAndAllSubEntityTypes()) {
									if (!entityType.isAbstractType()) {

										UniTransformation<EntityType, ObjectNode> ab = typeTransformation.getAB();
										ObjectNode value = ab.convert(entityType, ctx);
										validTypesArray.add(value);
									}
								}
								b.put("validTypes", validTypesArray);

							}
						}

					}

					@Override
					public void mergeBA(ObjectNode b, Attribute a, TransformationContext ctx) {
					}

				}).to().addMultiAssociationAttribute("validTypes", schemaType, CollectionSortType.ORDERABLE);
	}

}
