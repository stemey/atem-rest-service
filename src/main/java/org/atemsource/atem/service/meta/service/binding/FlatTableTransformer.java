package org.atemsource.atem.service.meta.service.binding;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.annotation.Cardinality;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.api.view.Visitor;
import org.atemsource.atem.service.meta.service.provider.TransformationFactory;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.visitor.HierachyVisitor;

public class FlatTableTransformer implements TransformationFactory {
	private DynamicEntityTypeSubrepository<?> dynamicEntityTypeSubrepository;

	private TransformationBuilderFactory transformationBuilderFactory;
	@Inject
	private EntityTypeRepository entityTypeRepository;

	public DynamicEntityTypeSubrepository<?> getDynamicEntityTypeSubrepository() {
		return dynamicEntityTypeSubrepository;
	}

	public void setDynamicEntityTypeSubrepository(DynamicEntityTypeSubrepository<?> dynamicEntityTypeSubrepository) {
		this.dynamicEntityTypeSubrepository = dynamicEntityTypeSubrepository;
	}

	public TransformationBuilderFactory getTransformationBuilderFactory() {
		return transformationBuilderFactory;
	}

	public void setTransformationBuilderFactory(TransformationBuilderFactory transformationBuilderFactory) {
		this.transformationBuilderFactory = transformationBuilderFactory;
	}

	public EntityTypeTransformation<?, ?> createTransformation(EntityType<?> originalType) {
		final EntityTypeBuilder targetBuilder = dynamicEntityTypeSubrepository
				.createBuilder(getFlatTypeCode(originalType));
		final TypeTransformationBuilder<?,?> builder = transformationBuilderFactory
				.create(originalType, targetBuilder);
		String path = "";
		HierachyVisitor.visit(originalType, new ViewVisitor<String>() {

			@Override
			public void visit(String context, Attribute attribute) {
				builder.transform().from(context + attribute.getCode()).to(attribute.getCode());
			}

			@Override
			public void visit(String context, Attribute attribute, Visitor<String> targetTypeVisitor) {
				if (attribute.getTargetCardinality() == Cardinality.ONE) {
					targetTypeVisitor.visit(context + attribute.getCode() + ".");
				}
			}

			@Override
			public void visitSubView(String context, View view, Visitor<String> subViewVisitor) {
			}

			@Override
			public void visitSuperView(String context, View view, Visitor<String> superViewVisitor) {
			}

		}, path);
		return builder.buildTypeTransformation();
	}

	protected String getFlatTypeCode(EntityType<?> originalType) {
		return "flattable:" + originalType.getCode();
	}

	@Override
	public <A, B> EntityTypeTransformation<A, B> getTransformation(EntityType<A> entityType) {
		EntityType<Object> flatType = entityTypeRepository.getEntityType(getFlatTypeCode(entityType));
		if (flatType != null) {
			DerivedType derivedType = (DerivedType) flatType.getMetaAttribute(DerivedType.META_ATTRIBUTE_CODE)
					.getValue(flatType);
			return (EntityTypeTransformation<A, B>) derivedType.getTransformation();
		} else {
			return (EntityTypeTransformation<A, B>) createTransformation(entityType);
		}
	}
}
