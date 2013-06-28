package org.atemsource.atem.service.meta.service.binding;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.service.meta.service.binding.attributetype.AttributeTransformationCreator;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Constant;
import org.atemsource.atem.utility.transform.impl.builder.Mixin;
import org.atemsource.atem.utility.transform.impl.builder.OneToOneAttributeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.TransformationFinder;
import org.codehaus.jackson.node.ObjectNode;

public abstract class AbstractAttributeTransformationFactory {

	private DynamicEntityTypeSubrepository subRepository;

	public void setAttributeMixins(List<AttributeMixin> attributeMixins) {
		this.attributeMixins = attributeMixins;
	}

	public void setSubRepository(DynamicEntityTypeSubrepository subRepository) {
		this.subRepository = subRepository;
	}

	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory) {
		this.transformationBuilderFactory = transformationBuilderFactory;
	}
	
	private String typeCodePrefix;

	private TransformationBuilderFactory transformationBuilderFactory;
	@Inject
	protected EntityTypeRepository entityTypeRepository;
	private EntityTypeTransformation attributeTransformation;
	private EntityTypeTransformation<Attribute, ?> arrayTransformation;
	private Map<AttributeTransformationCreator, EntityTypeTransformation<?, ?>> singleTransformations = new HashMap<AttributeTransformationCreator, EntityTypeTransformation<?, ?>>();
	private Map<AttributeTransformationCreator, EntityTypeTransformation<?, ?>> arrayTransformations = new HashMap<AttributeTransformationCreator, EntityTypeTransformation<?, ?>>();
	private Set<AttributeTransformationCreator> attributeCreators;
	private List<AttributeMixin> attributeMixins;

	private EntityTypeTransformation complexSingleTransformation;

	private EntityTypeTransformation complexArrayTransformation;

	public AbstractAttributeTransformationFactory() {
		super();
	}

	public void setAttributeCreators(Set<AttributeTransformationCreator> attributeCreators) {
		this.attributeCreators = attributeCreators;
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void init(EntityTypeTransformation complexTypeTransformation) {
	
		TypeTransformationBuilder<Attribute, ?> transformationBuilder = createAttributeTransformation();
		attributeTransformation = transformationBuilder.getReference();
		
		
		attributeTransformation.setFinder(new TransformationFinder<Attribute, ObjectNode>() {
	
			@Override
			public UniTransformation getAB(Attribute a, TransformationContext ctx) {
				Class<?> type = a.getTargetType().getJavaType();
				if (a instanceof CollectionAttribute) {
					for (AttributeTransformationCreator creator:attributeCreators) {
						if (creator.canTransform(a)) {
							return arrayTransformations.get(creator).getAB();
						}
					}
					if (a.getTargetType() instanceof EntityType<?>) {
						return complexArrayTransformation.getAB();
					} else {
						return null;
					}
				} else {
					for (AttributeTransformationCreator creator:attributeCreators) {
						if (creator.canTransform(a)) {
							return singleTransformations.get(creator).getAB();
						}
					}
					 if (a.getTargetType() instanceof EntityType<?>) {
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
		
		if (attributeMixins != null) {
			for (AttributeMixin mixin : attributeMixins) {
				mixin.mixin(transformationBuilder);
			}
		}

		transformationBuilder.buildTypeTransformation();
		
		complexSingleTransformation = createComplexTransformation(
				complexTypeTransformation, false);
		complexArrayTransformation = createComplexTransformation(
				complexTypeTransformation, true);
		
		
		createPrimitiveAttributes(true);
		createPrimitiveAttributes(false);

	
	}

	protected EntityTypeTransformation createComplexTransformation(EntityTypeTransformation complexTypeTransformation, boolean array) {
		EntityTypeBuilder typeBuilder = subRepository.createBuilder(typeCodePrefix+":"+"complex" + (array ? "-array" : ""));
		TypeTransformationBuilder<Attribute, ?> transformationBuilder = transformationBuilderFactory.create(
				Attribute.class, typeBuilder);
		if (array) {
			transformationBuilder.transformCustom(Mixin.class).transform(getArrayTransformation());
		}
		transformComplexAttribute(complexTypeTransformation, transformationBuilder);
		transformationBuilder.includeSuper(attributeTransformation);
		transformationBuilder.buildTypeTransformation();
		return transformationBuilder.getReference();
	}

	protected abstract  void transformComplexAttribute(EntityTypeTransformation complexTypeTransformation,
			TypeTransformationBuilder<Attribute, ?> transformationBuilder);

	protected void createPrimitiveAttributes(boolean array) {
		for (AttributeTransformationCreator creator : attributeCreators) {
			EntityTypeBuilder typeBuilder = subRepository.createBuilder(typeCodePrefix+":"+creator.getTargetName()
					+ (array ? "-array" : ""));
			TypeTransformationBuilder<Attribute, ?> transformationBuilder = transformationBuilderFactory.create(
					Attribute.class, typeBuilder);
			if (array) {
				transformationBuilder.transformCustom(Mixin.class).transform(getArrayTransformation());
			}
			transformationBuilder.includeSuper(attributeTransformation);
			creator.addAttributeTransformation(transformationBuilder);
			if (array) {
				arrayTransformations.put(creator, transformationBuilder.getReference());
			} else {
				singleTransformations.put(creator, transformationBuilder.getReference());
			}
		}
	}

	private EntityTypeTransformation<?, ?> getArrayTransformation() {
		if (arrayTransformation == null) {
			EntityTypeBuilder typeBuilder = subRepository.createBuilder(typeCodePrefix+":"+"array");
			TypeTransformationBuilder<Attribute, ?> transformationBuilder = transformationBuilderFactory.create(
					Attribute.class, typeBuilder);
			transformationBuilder.transformCustom(Constant.class).to("array").value(Boolean.class, true);
			this.arrayTransformation = transformationBuilder.buildTypeTransformation();
		}
		return arrayTransformation;
	}

	public String getTypeCodePrefix() {
		return typeCodePrefix;
	}

	public void setTypeCodePrefix(String typeCodePrefix) {
		this.typeCodePrefix = typeCodePrefix;
	}

	private TypeTransformationBuilder<Attribute, ?> createAttributeTransformation() {
		EntityTypeBuilder typeBuilder = subRepository.createBuilder(typeCodePrefix+":"+"attribute");
		TypeTransformationBuilder<Attribute, ?> transformationBuilder = transformationBuilderFactory.create(
				Attribute.class, typeBuilder);
		OneToOneAttributeTransformationBuilder<Attribute, ?, ?> requiredTransformer = transformationBuilder.transform();
		requiredTransformer.from("required");
		requiredTransformer.to("required");
	
		transformationBuilder.transform().from("code").to("code");
		extendAttribute(transformationBuilder);
	
		return transformationBuilder;
	}

	protected void extendAttribute(TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
	
	}

	public EntityTypeTransformation<?, ?> getReference() {
		return attributeTransformation;
	}

	public EntityTypeTransformation<?, ?> getAttributeTransformation() {
		return attributeTransformation;
	}

}