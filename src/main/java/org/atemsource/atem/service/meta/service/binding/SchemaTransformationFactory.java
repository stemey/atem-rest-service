package org.atemsource.atem.service.meta.service.binding;

import java.util.Map;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.apache.log4j.Logger;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.attribute.relation.ListAssociationAttribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.api.type.primitive.ChoiceType;
import org.atemsource.atem.api.type.primitive.IntegerType;
import org.atemsource.atem.api.type.primitive.TextType;
import org.atemsource.atem.impl.common.AbstractEntityTypeBuilder;
import org.atemsource.atem.impl.json.JsonEntityTypeImpl;
import org.atemsource.atem.impl.json.JsonUtils;
import org.atemsource.atem.service.meta.service.annotation.ValidTypes;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.Converter;
import org.atemsource.atem.utility.transform.api.JavaConverter;
import org.atemsource.atem.utility.transform.api.JavaTransformation;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.api.constraint.DateFormat;
import org.atemsource.atem.utility.transform.api.constraint.PossibleValues;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.GenericTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.OneToOneAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.TransformationTargetTypeBuilder;
import org.atemsource.atem.utility.transform.impl.converter.ConverterUtils;
import org.atemsource.atem.utility.transform.impl.converter.StringPossibleValues;
import org.atemsource.atem.utility.validation.ValidationVisitor;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

/**
 * Creates the transformation from EntityType to restclient editor schema.
 * 
 * @author stemey
 */
public class SchemaTransformationFactory {
	private static final String SINGLE_ATTRIBUTE_TYPE_CODE = "single-attribute";

	private static final String COLLECTION_ATTRIBUTE_TYPE_CODE = "array-attribute";

	private static final String ATTRIBUTE_TYPE_CODE = "attribute";

	private static final String TEXT_TYPE_CODE = "text-type";

	private static final String PRIMITIVE_TYPE_CODE = "primitive-type";

	private static final String LIST_ATTRIBUTE_TYPE_CODE = "list-type";

	private static final String INTEGER_TYPE_CODE = "integer-type";

	private static final String TYPE_TYPE_CODE = "basic-type";

	private static final String SCHEMA_TYPE_CODE = "schema";
	private static final String TYPE_REF_TYPE_CODE = "type-ref";

	private static final String ENTITYTYPE_REF_TYPE_CODE = "entitytype-ref";

	private static final String PRIMITIVETYPE_REF_TYPE_CODE = "primitivetype-ref";

	private static Logger logger = Logger.getLogger(SchemaTransformationFactory.class);

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private EntityTypeTransformation<EntityType, ?> schemaTransformation;

	private DynamicEntityTypeSubrepository<?> subrepository;

	private TransformationBuilderFactory transformationBuilderFactory;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void addArrayTransform(TypeTransformationBuilder<?, ?> transformationBuilder, final String attribute,
			final Class targetType, final Object value) {
		GenericTransformationBuilder arrayTransform = transformationBuilder
				.transformCustom(GenericTransformationBuilder.class);
		arrayTransform.to().addSingleAttribute(attribute, targetType);
		arrayTransform.transform(new JavaTransformation<Object, ObjectNode>() {

			@Override
			public void mergeAB(Object a, ObjectNode b, TransformationContext ctx) {
				b.put(attribute, JsonUtils.convertToJson(value));
			}

			@Override
			public void mergeBA(ObjectNode b, Object a, TransformationContext ctx) {
			}

		});
	}

	private String typeCodePrefix = "schema:";

	@SuppressWarnings("unchecked")
	private void addValuesTransform(TypeTransformationBuilder<?, ?> transformationBuilder, final String attribute,
			final Class targetType) {
		GenericTransformationBuilder arrayTransform = transformationBuilder
				.transformCustom(GenericTransformationBuilder.class);
		Type type = entityTypeRepository.getType(targetType);
		arrayTransform.to().addMultiAssociationAttribute(attribute, type, CollectionSortType.ORDERABLE);
		arrayTransform.transform(new JavaTransformation<Object, ObjectNode>() {

			@Override
			public void mergeAB(Object a, ObjectNode b, TransformationContext ctx) {
				ChoiceType<?> choiceType = (ChoiceType<?>) a;
				ArrayNode arrayNode = b.arrayNode();
				for (Map.Entry<String, ?> entry : choiceType.getOptionsMap().entrySet()) {
					arrayNode.add(String.valueOf(entry.getValue()));
				}
				b.put(attribute, arrayNode);
			}

			@Override
			public void mergeBA(ObjectNode b, Object a, TransformationContext ctx) {
			}

		});
	}

	private void createIntegerTypeTransformation(EntityTypeTransformation<Type, ?> superTransformation) {
		EntityTypeBuilder typeBuilder = subrepository.createBuilder(typeCodePrefix + INTEGER_TYPE_CODE);
		TypeTransformationBuilder<IntegerType, ?> transformationBuilder = transformationBuilderFactory.create(
				IntegerType.class, typeBuilder);
		transformationBuilder.includeSuper(superTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	private void createListTypeTransformation(EntityTypeTransformation<Type, ?> superTransformation) {
		EntityTypeBuilder typeBuilder = subrepository.createBuilder(typeCodePrefix + LIST_ATTRIBUTE_TYPE_CODE);
		TypeTransformationBuilder<ListAssociationAttribute, ?> transformationBuilder = transformationBuilderFactory
				.create(ListAssociationAttribute.class, typeBuilder);
		transformationBuilder.includeSuper(superTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	protected EntityTypeTransformation<?, ?> createPrimitiveTypeTransformation(
			EntityTypeTransformation typeTransformation) {
		EntityTypeBuilder typeBuilder = subrepository.createBuilder(typeCodePrefix + PRIMITIVE_TYPE_CODE);
		TypeTransformationBuilder<Type, ?> transformationBuilder = transformationBuilderFactory.create(Type.class,
				typeBuilder);
		createTextTypeTransformation((EntityTypeTransformation<Type, ?>) transformationBuilder.getReference());
		createIntegerTypeTransformation((EntityTypeTransformation<Type, ?>) transformationBuilder.getReference());
		transformationBuilder.includeSuper(typeTransformation);
		return transformationBuilder.buildTypeTransformation();
	}

	private void createTextTypeTransformation(EntityTypeTransformation<Type, ?> superTransformation) {
		EntityTypeBuilder typeBuilder = subrepository.createBuilder(typeCodePrefix + TEXT_TYPE_CODE);
		TypeTransformationBuilder<TextType, ?> transformationBuilder = transformationBuilderFactory.create(
				TextType.class, typeBuilder);
		transformationBuilder.transform().to("max-length").from("maxLength");
		transformationBuilder.includeSuper(superTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	private Converter<Attribute<?, ?>, String> getAttributeKeyConverter() {
		JavaConverter<Attribute<?, ?>, String> javaConverter = new JavaConverter<Attribute<?, ?>, String>() {

			@Override
			public String convertAB(Attribute a, TransformationContext ctx) {
				return a.getCode();
			}

			@Override
			public Attribute convertBA(String b, TransformationContext ctx) {
				return null;
			}
		};
		return ConverterUtils.create(javaConverter);
	}

	private Converter<Object, Object> createAttributeTransformation(EntityTypeTransformation typeTransformation,
			EntityTypeTransformation typeRefTransformation,EntityType typeRef) {
		EntityTypeBuilder typeBuilder = subrepository.createBuilder(typeCodePrefix + ATTRIBUTE_TYPE_CODE);
		TypeTransformationBuilder<Attribute, ?> transformationBuilder = transformationBuilderFactory.create(
				Attribute.class, typeBuilder);
		OneToOneAttributeTransformationBuilder<Attribute, ?, ?> requiredTransformer = transformationBuilder.transform();
		requiredTransformer.from("required");
		requiredTransformer.to("required");
		// transformationBuilder.transform().from("code").to("label");
		transformationBuilder.transform().from("targetType").to("type").convert(typeRefTransformation);

		transformValidTypes(transformationBuilder, typeRefTransformation, typeRef);

		transformationBuilder.transform().from("code").to("label");
		transformationBuilder.transform().from("code").to("code");

		transformationBuilder.transformCollection().to("values")
				.from("@" + PossibleValues.META_ATTRIBUTE_CODE + ".values").convertEmptyToNull();
		transformationBuilder.transform().to("dateformat").from("@" + DateFormat.META_ATTRIBUTE_CODE + ".pattern");

		createSingleAttributeTransformation(transformationBuilder.getReference(), typeRefTransformation);
		createListAttributeTransformation(transformationBuilder.getReference(), typeRefTransformation);
		return transformationBuilder.buildTypeTransformation();
	}

	private void createListAttributeTransformation(EntityTypeTransformation attributeTransformation,
			EntityTypeTransformation typeRefTransformation) {
		EntityTypeBuilder typeBuilder = subrepository.createBuilder(typeCodePrefix + COLLECTION_ATTRIBUTE_TYPE_CODE);
		TypeTransformationBuilder<CollectionAttribute, ?> transformationBuilder = transformationBuilderFactory.create(
				CollectionAttribute.class, typeBuilder);
		addArrayTransform(transformationBuilder, "array", Boolean.class, true);
		transformationBuilder.includeSuper(attributeTransformation);
		transformationBuilder.buildTypeTransformation();
	}

	private void createSingleAttributeTransformation(EntityTypeTransformation attributeTransformation,
			EntityTypeTransformation typeRefTransformation) {
		EntityTypeBuilder typeBuilder = subrepository.createBuilder(typeCodePrefix + SINGLE_ATTRIBUTE_TYPE_CODE);
		TypeTransformationBuilder<SingleAttribute, ?> transformationBuilder = transformationBuilderFactory.create(
				SingleAttribute.class, typeBuilder);
		transformationBuilder.includeSuper(attributeTransformation);
		addArrayTransform(transformationBuilder, "array", Boolean.class, false);
		transformationBuilder.buildTypeTransformation();
	}

	public DynamicEntityTypeSubrepository<?> getSubrepository() {
		return subrepository;
	}

	public EntityTypeTransformation<EntityType, ?> getTransformation() {
		return schemaTransformation;
	}

	public TransformationBuilderFactory getTransformationBuilderFactory() {
		return transformationBuilderFactory;
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		EntityTypeBuilder typeRefBuilder = subrepository.createBuilder(typeCodePrefix + TYPE_REF_TYPE_CODE);
		TypeTransformationBuilder<EntityType,?> typeRefTransformationBuilder = transformationBuilderFactory.create(EntityType.class,
				typeRefBuilder);
		createPrimitiveTypeRefTransformation(typeRefTransformationBuilder.getReference());
		EntityTypeTransformation<EntityType, ?> typeRefTransformation = createEntityTypeRefTransformation(typeRefTransformationBuilder.getReference());
		EntityTypeTransformation<EntityType, ?> entitytypeRefTransformation = typeRefTransformationBuilder.buildTypeTransformation();
		
		
		
		
		EntityTypeBuilder schemaBuilder = subrepository.createBuilder(typeCodePrefix + SCHEMA_TYPE_CODE);

		EntityTypeBuilder basicTypeBuilder = subrepository.createBuilder(typeCodePrefix + TYPE_TYPE_CODE);
		TypeTransformationBuilder<Type, ?> basicTransformationBuilder = transformationBuilderFactory.create(Type.class,
				basicTypeBuilder);
		transformIdsOfType(basicTransformationBuilder);

		EntityTypeTransformation typeTransformation = basicTransformationBuilder.getReference();

		EntityTypeTransformation primitiveTypeTransformation = createPrimitiveTypeTransformation(typeTransformation);

		final TypeTransformationBuilder<EntityType, ?> schemaTransformationBuilder = transformationBuilderFactory
				.create(EntityType.class, schemaBuilder);
		schemaTransformation = (EntityTypeTransformation<EntityType, ?>) schemaTransformationBuilder.getReference();

		schemaTransformationBuilder.transformCollection().from("attributes").to("attributes")
				.convert(createAttributeTransformation(typeTransformation, typeRefTransformation,typeRefTransformation.getEntityTypeB()));

		schemaTransformationBuilder.transform().from("superEntityType").to("extends").convert(entitytypeRefTransformation);
		
		basicTransformationBuilder.buildTypeTransformation();
		schemaTransformationBuilder.includeSuper(typeTransformation);

		schemaTransformationBuilder.transformCustom(GenericTransformationBuilder.class)
				.transform(new JavaTransformation<EntityType, ObjectNode>() {

					@Override
					public void mergeAB(EntityType a, ObjectNode b, TransformationContext ctx) {
						b.put("type_property", "ext_type");
					}

					@Override
					public void mergeBA(ObjectNode b, EntityType a, TransformationContext ctx) {
					}

				}).to().addSingleAttribute("type-property", entityTypeRepository.getType(String.class));

		schemaTransformationBuilder.buildTypeTransformation();

	}

	private void createPrimitiveTypeRefTransformation(EntityTypeTransformation<?, ?> superTypeRefTransformation) {
		EntityTypeBuilder typeRefBuilder = subrepository.createBuilder(typeCodePrefix + PRIMITIVETYPE_REF_TYPE_CODE);
		TypeTransformationBuilder<EntityType,?> typeRefTransformationBuilder = transformationBuilderFactory.create(EntityType.class,
				typeRefBuilder);
		typeRefTransformationBuilder.transform().from("code");
		
		typeRefTransformationBuilder.includeSuper(superTypeRefTransformation);
		
		EntityTypeTransformation<EntityType, ?> typeRefTransformation = typeRefTransformationBuilder.buildTypeTransformation();
		
		Attribute attribute = ((EntityType)typeRefTransformation.getTypeB()).getAttribute("code");
		Attribute metaAttribute = entityTypeRepository.getEntityType(Attribute.class).getMetaAttribute(PossibleValues.META_ATTRIBUTE_CODE);
		
		
		String[] values = new String[]{//
				getCode(Boolean.class),//
				getCode(Long.class),//
				getCode(String.class),//
				getCode(Integer.class)//
				};
		
		metaAttribute.setValue(attribute, new StringPossibleValues(values));
	}
	
	private EntityTypeTransformation<EntityType, ?> createEntityTypeRefTransformation(EntityTypeTransformation<?, ?> superTypeRefTransformation) {
		EntityTypeBuilder typeRefBuilder = subrepository.createBuilder(typeCodePrefix + ENTITYTYPE_REF_TYPE_CODE);
		TypeTransformationBuilder<EntityType,?> typeRefTransformationBuilder = transformationBuilderFactory.create(EntityType.class,
				typeRefBuilder);
		typeRefTransformationBuilder.transform().from("code");
		
		typeRefTransformationBuilder.includeSuper(superTypeRefTransformation);
		
		return typeRefTransformationBuilder.buildTypeTransformation();
	
	}
	
	private String getCode(Class clazz) {
		return entityTypeRepository.getType(clazz).getCode();
	}

	public void setSubrepository(DynamicEntityTypeSubrepository<?> subrepository) {
		this.subrepository = subrepository;
	}

	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory) {
		this.transformationBuilderFactory = transformationBuilderFactory;
	}

	@SuppressWarnings("unchecked")
	private void transformIdsOfType(TypeTransformationBuilder<Type, ?> basicTransformationBuilder) {
		TransformationTargetTypeBuilder typeBuilder = basicTransformationBuilder
				.transformCustom(GenericTransformationBuilder.class).from("code")
				.transform(new JavaTransformation<Type, ObjectNode>() {

					@Override
					public void mergeAB(Type a, ObjectNode b, TransformationContext ctx) {
						if (a instanceof JsonEntityTypeImpl) {
							b.put("code", ((JsonEntityTypeImpl) a).getExternalTypeCode());
							b.put("label", ((JsonEntityTypeImpl) a).getExternalTypeCode());
						} else {
							b.put("code", a.getCode());
							b.put("label", a.getCode());
						}
					}

					@Override
					public void mergeBA(ObjectNode b, Type a, TransformationContext ctx) {
					}
				}).to();
		typeBuilder.addSingleAttribute("code", String.class);
		typeBuilder.addSingleAttribute("label", String.class);
	}

	@SuppressWarnings("unchecked")
	private void transformValidTypes(TypeTransformationBuilder<Attribute, ?> attributeTransformationBuilder,
			final EntityTypeTransformation<EntityType, ObjectNode> typeTransformation,
			final EntityType<?> typeRef) {
		attributeTransformationBuilder
				.transformCustom(GenericTransformationBuilder.class)
				.transform(new JavaTransformation<Attribute<?, ?>, ObjectNode>() {

					@Override
					public void mergeAB(Attribute<?, ?> a, ObjectNode b, TransformationContext ctx) {
						logger.debug("transforming " + a.getEntityType().getEntityClass().getSimpleName() + "."
								+ a.getCode());

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

				})
				.to()
				.addMultiAssociationAttribute("validTypes",
						typeRef, CollectionSortType.ORDERABLE);
	}
}