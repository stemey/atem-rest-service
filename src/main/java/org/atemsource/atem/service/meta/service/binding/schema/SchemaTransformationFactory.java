package org.atemsource.atem.service.meta.service.binding.schema;

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
import org.atemsource.atem.service.meta.service.binding.AbstractAttributeTransformationFactory;
import org.atemsource.atem.service.meta.service.binding.editor.EditorTransformationFactory;
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
import org.atemsource.atem.utility.transform.api.meta.Binding;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Constant;
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
	private static final String SCHEMA_TYPE_CODE = "schema";

	private static Logger logger = Logger.getLogger(EditorTransformationFactory.class);

	@Inject
	private EntityTypeRepository entityTypeRepository;

	private EntityTypeTransformation<EntityType, ?> schemaTransformation;

	private DynamicEntityTypeSubrepository<?> subrepository;

	private TransformationBuilderFactory transformationBuilderFactory;

	private String typeCodePrefix = "schema:";

	public EntityTypeTransformation<EntityType, ?> getTransformation() {
		return schemaTransformation;
	}

	private AbstractAttributeTransformationFactory attributeTransformationFactory;

	public AbstractAttributeTransformationFactory getAttributeTransformationFactory() {
		return attributeTransformationFactory;
	}

	public void setAttributeTransformationFactory(AbstractAttributeTransformationFactory attributeTransformationFactory) {
		this.attributeTransformationFactory = attributeTransformationFactory;
	}

	@SuppressWarnings("unchecked")
	@PostConstruct
	public void init() {
		EntityTypeBuilder schemaBuilder = subrepository.createBuilder(typeCodePrefix + SCHEMA_TYPE_CODE);

		final TypeTransformationBuilder<EntityType, ?> schemaTransformationBuilder = transformationBuilderFactory
				.create(EntityType.class, schemaBuilder);
		schemaTransformation = (EntityTypeTransformation<EntityType, ?>) schemaTransformationBuilder.getReference();

		EntityTypeTransformation<?, ?> typeRefTransformation = createTypeRefTransformation();
		attributeTransformationFactory.init(typeRefTransformation);

		transformTypeCode(schemaTransformationBuilder);
		schemaTransformationBuilder.transformCustom(Constant.class).to("type-property").value(String.class, "ext_type");
		schemaTransformationBuilder.transform().from("superEntityType").to("super-type").convert(typeRefTransformation);
		schemaTransformationBuilder.transformCollection().from("attributes").to("attributes")
				.convert(attributeTransformationFactory.getAttributeTransformation());

		schemaTransformationBuilder.buildTypeTransformation();

	}
	
	public EntityTypeTransformation<?,?> createTypeRefTransformation() {
		EntityTypeBuilder schemaBuilder = subrepository.createBuilder(typeCodePrefix + "type-ref");

		final TypeTransformationBuilder<EntityType, ?> schemaTransformationBuilder = transformationBuilderFactory
				.create(EntityType.class, schemaBuilder);
		schemaTransformationBuilder.transform().from("code").to("$ref");
		return schemaTransformationBuilder.buildTypeTransformation();
	}

	protected void transformTypeCode(final TypeTransformationBuilder<EntityType, ?> schemaTransformationBuilder) {
		if (schemaTransformationBuilder.getSourceType().getMetaAttribute(Binding.META_ATTRIBUTE_CODE) != null) {
			schemaTransformationBuilder.transform().from("code").to("code");
			// if binding exists then 'code' will be overwritten.
			schemaTransformationBuilder.transform().from("@" + Binding.META_ATTRIBUTE_CODE + ".externalTypeCode")
					.to("code");
		} else {
			schemaTransformationBuilder.transform().from("code").to("code");
		}

	}

	public void setSubrepository(DynamicEntityTypeSubrepository<?> subrepository) {
		this.subrepository = subrepository;
	}

	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory) {
		this.transformationBuilderFactory = transformationBuilderFactory;
	}
}
