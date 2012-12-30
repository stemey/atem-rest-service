package org.atemsource.atem.service.meta.service.binding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.primitive.NumberType;
import org.atemsource.atem.service.meta.service.annotation.ValidTypes;
import org.atemsource.atem.service.meta.service.binding.attributetype.AttributeTransformationCreator;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.JavaTransformation;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Constant;
import org.atemsource.atem.utility.transform.impl.builder.GenericTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.Mixin;
import org.atemsource.atem.utility.transform.impl.builder.OneToOneAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.TransformationFinder;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

/**
 * 
 * array: minNumber, maxNumber,
 * 
 * number: numberformat,min,max string: pattern boolean: <other>: valid-types
 * 
 * 
 * attribute.targetType -> attribute mixin attribute -> array mixin
 * 
 * 
 * @author eee
 * 
 */
public class AttributeTransformationFactory {

	public void setAttributeMixins(List<AttributeMixin> attributeMixins) {
		this.attributeMixins = attributeMixins;
	}

	public DynamicEntityTypeSubrepository getSubRepository() {
		return subRepository;
	}

	public void setSubRepository(DynamicEntityTypeSubrepository subRepository) {
		this.subRepository = subRepository;
	}

	public TransformationBuilderFactory getTransformationBuilderFactory() {
		return transformationBuilderFactory;
	}

	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory) {
		this.transformationBuilderFactory = transformationBuilderFactory;
	}

	private DynamicEntityTypeSubrepository subRepository;
	private TransformationBuilderFactory transformationBuilderFactory;
	@Inject
	private EntityTypeRepository entityTypeRepository;
	private EntityTypeTransformation attributeTransformation;
	private EntityTypeTransformation<Attribute, ?> arrayTransformation;

	private Map<Class<?>, EntityTypeTransformation<?, ?>> singleTransformations = new HashMap<Class<?>, EntityTypeTransformation<?, ?>>();
	private Map<Class<?>, EntityTypeTransformation<?, ?>> arrayTransformations = new HashMap<Class<?>, EntityTypeTransformation<?, ?>>();

	private Set<AttributeTransformationCreator> attributeCreators;
	private List<AttributeMixin> attributeMixins;

	public void setAttributeCreators(Set<AttributeTransformationCreator> attributeCreators) {
		this.attributeCreators = attributeCreators;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void init(EntityTypeTransformation complexTypeTransformation) {

		TypeTransformationBuilder<Attribute, ?> transformationBuilder = createAttributeTransformation();
		attributeTransformation = transformationBuilder.getReference();
		final EntityTypeTransformation complexSingleTransformation = createComplexTransformation(
				complexTypeTransformation, false);
		final EntityTypeTransformation complexArrayTransformation = createComplexTransformation(
				complexTypeTransformation, true);
		if (attributeMixins != null) {
			for (AttributeMixin mixin : attributeMixins) {
				mixin.mixin(transformationBuilder);
			}
		}
		transformValidTypes(transformationBuilder, complexTypeTransformation, complexTypeTransformation.getEntityTypeB());
		createAttributes(true);
		createAttributes(false);
		attributeTransformation.setFinder(new TransformationFinder<Attribute, ObjectNode>() {

			@Override
			public UniTransformation getAB(Attribute a, TransformationContext ctx) {
				Class<?> type = a.getTargetType().getJavaType();
				if (a instanceof CollectionAttribute) {
					EntityTypeTransformation<?, ?> transformation = arrayTransformations.get(type);
					if (transformation != null) {
						return transformation.getAB();
					} else if (a.getTargetType() instanceof EntityType<?>) {
						return complexArrayTransformation.getAB();
					} else {
						return null;
					}
				} else {
					EntityTypeTransformation<?, ?> transformation = singleTransformations.get(type);
					if (transformation != null) {
						return transformation.getAB();
					} else if (a.getTargetType() instanceof EntityType<?>) {
						return complexSingleTransformation.getAB();
					} else {
						return null;
					}
				}
			}

			@Override
			public UniTransformation getBA(ObjectNode b, TransformationContext ctx) {
				return null;
			}
		});
		transformationBuilder.buildTypeTransformation();

	}

	private EntityTypeTransformation createComplexTransformation(EntityTypeTransformation complexTypeTransformation,
			boolean array) {
		EntityTypeBuilder typeBuilder = subRepository.createBuilder("complex" + (array ? "-array" : ""));
		TypeTransformationBuilder<Attribute, ?> transformationBuilder = transformationBuilderFactory.create(
				Attribute.class, typeBuilder);
		if (array) {
			transformationBuilder.transformCustom(Mixin.class).transform(getArrayTransformation());
		}
		transformationBuilder.transform().from("targetType").to("type").convert(complexTypeTransformation);
		transformationBuilder.includeSuper(attributeTransformation);
		transformationBuilder.buildTypeTransformation();
		return transformationBuilder.getReference();
	}
	
	

	protected void createAttributes(boolean array) {
		for (AttributeTransformationCreator creator : attributeCreators) {
			EntityTypeBuilder typeBuilder = subRepository.createBuilder(creator.getType().getName()
					+ (array ? "-array" : ""));
			TypeTransformationBuilder<Attribute, ?> transformationBuilder = transformationBuilderFactory.create(
					Attribute.class, typeBuilder);
			if (array) {
				transformationBuilder.transformCustom(Mixin.class).transform(getArrayTransformation());
			}
			transformationBuilder.includeSuper(attributeTransformation);
			creator.addAttributeTransformation(transformationBuilder);
			if (array) {
				arrayTransformations.put(creator.getType(), transformationBuilder.getReference());
			} else {
				singleTransformations.put(creator.getType(), transformationBuilder.getReference());
			}
		}
	}

	private EntityTypeTransformation<?, ?> getArrayTransformation() {
		if (arrayTransformation == null) {
			EntityTypeBuilder typeBuilder = subRepository.createBuilder("array");
			TypeTransformationBuilder<Attribute, ?> transformationBuilder = transformationBuilderFactory.create(
					Attribute.class, typeBuilder);
			transformationBuilder.transformCustom(Constant.class).to("array").value(Boolean.class, true);
			this.arrayTransformation = transformationBuilder.buildTypeTransformation();
		}
		return arrayTransformation;
	}

	private TypeTransformationBuilder<Attribute, ?> createAttributeTransformation() {
		EntityTypeBuilder typeBuilder = subRepository.createBuilder("attribute");
		TypeTransformationBuilder<Attribute, ?> transformationBuilder = transformationBuilderFactory.create(
				Attribute.class, typeBuilder);
		OneToOneAttributeTransformationBuilder<Attribute, ?, ?> requiredTransformer = transformationBuilder.transform();
		requiredTransformer.from("required");
		requiredTransformer.to("required");
		// transformationBuilder.transform().from("targetType").to("type").convert(typeTransformation);

		// transformValidTypes(transformationBuilder, typeTransformation,
		// schemaType);

		transformationBuilder.transform().from("code").to("code");
		extendAttribute(transformationBuilder);

		// transformationBuilder.transformCollection().to("values")
		// .from("@" + PossibleValues.META_ATTRIBUTE_CODE +
		// ".values").convertEmptyToNull();
		// transformationBuilder.transform().to("dateformat").from("@" +
		// DateFormat.META_ATTRIBUTE_CODE + ".pattern");
		// transformationBuilder.transformCustom(GenericTransformationBuilder.class).transform(getFractionTransformation()).to().addSingleAttribute("fraction",
		// Integer.class);

		// createSingleAttributeTransformation(transformationBuilder.getReference());
		// createListAttributeTransformation(transformationBuilder.getReference());
		return transformationBuilder;
	}

	protected void extendAttribute(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {

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
								if (validTypesArray.size() > 0) {
									b.put("validTypes", validTypesArray);
								}
							}
						}

					}

					@Override
					public void mergeBA(ObjectNode b, Attribute a, TransformationContext ctx) {
					}

				}).to().addMultiAssociationAttribute("validTypes", schemaType, CollectionSortType.ORDERABLE);
	}

	// private void createListAttributeTransformation(EntityTypeTransformation
	// attributeTransformation) {
	// EntityTypeBuilder typeBuilder =
	// subrepository.createBuilder(typeCodePrefix +
	// COLLECTION_ATTRIBUTE_TYPE_CODE);
	// TypeTransformationBuilder<CollectionAttribute, ?> transformationBuilder =
	// transformationBuilderFactory.create(
	// CollectionAttribute.class, typeBuilder);
	// addArrayTransform(transformationBuilder, "array", Boolean.class, true);
	// transformationBuilder.includeSuper(attributeTransformation);
	// transformationBuilder.buildTypeTransformation();
	// }
	//
	// private void createSingleAttributeTransformation(EntityTypeTransformation
	// attributeTransformation) {
	// EntityTypeBuilder typeBuilder =
	// subrepository.createBuilder(typeCodePrefix + SINGLE_ATTRIBUTE_TYPE_CODE);
	// TypeTransformationBuilder<SingleAttribute, ?> transformationBuilder =
	// transformationBuilderFactory.create(
	// SingleAttribute.class, typeBuilder);
	// transformationBuilder.includeSuper(attributeTransformation);
	// addArrayTransform(transformationBuilder, "array", Boolean.class, false);
	// transformationBuilder.buildTypeTransformation();
	// }
	public EntityTypeTransformation<?, ?> getReference() {
		return attributeTransformation;
	}

	public  EntityTypeTransformation<?, ?> getAttributeTransformation() {
		return attributeTransformation;
	}

}
