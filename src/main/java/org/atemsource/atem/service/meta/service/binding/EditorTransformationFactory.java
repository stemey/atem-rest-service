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
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.GenericTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.OneToOneAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.TransformationTargetTypeBuilder;
import org.atemsource.atem.utility.transform.impl.converter.ConverterUtils;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

/**
 * Creates the transformation from EntityType to restclient editor schema.
 * 
 * @author stemey
 */
public class EditorTransformationFactory {
	private static final String SINGLE_ATTRIBUTE_TYPE_CODE = "single-attribute";

	private static final String COLLECTION_ATTRIBUTE_TYPE_CODE = "array-attribute";

	private static final String ATTRIBUTE_TYPE_CODE = "attribute";

	private static final String TEXT_TYPE_CODE = "text-type";

	private static final String PRIMITIVE_TYPE_CODE = "primitive-type";

	private static final String LIST_ATTRIBUTE_TYPE_CODE = "list-type";

	private static final String INTEGER_TYPE_CODE = "integer-type";

	private static final String TYPE_TYPE_CODE = "basic-type";

	private static final String SCHEMA_TYPE_CODE = "schema";

	private static Logger logger = Logger.getLogger(EditorTransformationFactory.class);

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

	private String typeCodePrefix = "editor:";

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
			EntityTypeTransformation schemaTransformation,EntityType schemaType) {
		EntityTypeBuilder typeBuilder = subrepository.createBuilder(typeCodePrefix + ATTRIBUTE_TYPE_CODE);
		TypeTransformationBuilder<Attribute, ?> transformationBuilder = transformationBuilderFactory.create(
				Attribute.class, typeBuilder);
		OneToOneAttributeTransformationBuilder<Attribute, ?, ?> requiredTransformer = transformationBuilder.transform();
		requiredTransformer.from("required");
		requiredTransformer.to("required");
		transformationBuilder.transform().from("targetType").to("type").convert(typeTransformation);

				transformationBuilder.transform().from("code").to("label");
		transformationBuilder.transform().from("code").to("code");

		transformationBuilder.transformCollection().to("values")
				.from("@" + PossibleValues.META_ATTRIBUTE_CODE + ".values").convertEmptyToNull();
		transformationBuilder.transform().to("dateformat").from("@" + DateFormat.META_ATTRIBUTE_CODE + ".pattern");

		createSingleAttributeTransformation(transformationBuilder.getReference(), schemaTransformation);
		createListAttributeTransformation(transformationBuilder.getReference(), schemaTransformation);
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
	
	private AttributeTransformationFactory attributeTransformationFactory;

	public AttributeTransformationFactory getAttributeTransformationFactory() {
		return attributeTransformationFactory;
	}

	public void setAttributeTransformationFactory(AttributeTransformationFactory attributeTransformationFactory) {
		this.attributeTransformationFactory = attributeTransformationFactory;
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		EntityTypeBuilder schemaBuilder = subrepository.createBuilder(typeCodePrefix + SCHEMA_TYPE_CODE);

		EntityTypeBuilder basicTypeBuilder = subrepository.createBuilder(typeCodePrefix + TYPE_TYPE_CODE);
		TypeTransformationBuilder<Type, ?> basicTransformationBuilder = transformationBuilderFactory.create(Type.class,
				basicTypeBuilder);
		transformIdsOfType(basicTransformationBuilder);

		EntityTypeTransformation typeTransformation = basicTransformationBuilder.getReference();

		final TypeTransformationBuilder<EntityType, ?> schemaTransformationBuilder = transformationBuilderFactory
				.create(EntityType.class, schemaBuilder);
		schemaTransformation = (EntityTypeTransformation<EntityType, ?>) schemaTransformationBuilder.getReference();
		
		attributeTransformationFactory.init(schemaTransformation);
		

		schemaTransformationBuilder.transformCollection().from("attributes").to("attributes")
				.convert(attributeTransformationFactory.getAttributeTransformation());
		

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


}
