package org.atemsource.atem.service.meta.type;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.service.FindByIdService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.api.view.Visitor;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.service.entity.ObservationService;
import org.atemsource.atem.utility.compare.Comparison;
import org.atemsource.atem.utility.compare.ComparisonBuilder;
import org.atemsource.atem.utility.compare.ComparisonBuilderFactory;
import org.atemsource.atem.utility.observer.EntityHandle;
import org.atemsource.atem.utility.observer.EntityObserver;
import org.atemsource.atem.utility.observer.EntityObserverDefinition;
import org.atemsource.atem.utility.observer.EntityObserverFactory;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.visitor.HierachyVisitor;

public class DerivedEntityObservationService implements ObservationService {

	private ComparisonBuilderFactory comparisonBuilderFactory;

	private EntityObserverFactory entityObserverFactory;

	protected Comparison createComparison(EntityType<?> entityType) {
		ComparisonBuilder comparisonBuilder = comparisonBuilderFactory.create(entityType);

		HierachyVisitor.visit(entityType, new ViewVisitor<ComparisonBuilder>() {

			@Override
			public void visit(ComparisonBuilder context, Attribute attribute) {
				context.include(attribute);
			}

			@Override
			public void visit(ComparisonBuilder context, Attribute attribute,
					Visitor<ComparisonBuilder> targetTypeVisitor) {
				ComparisonBuilder cascade = context.include(attribute).cascade();
				targetTypeVisitor.visit(cascade);
			}

			@Override
			public void visitSubView(ComparisonBuilder context, View view, Visitor<ComparisonBuilder> subViewVisitor) {
			}

			@Override
			public void visitSuperView(ComparisonBuilder context, View view, Visitor<ComparisonBuilder> superViewVisitor) {
			}

		}, comparisonBuilder);
		return comparisonBuilder.create();
	}

	@Override
	public EntityObserver createObserver(final EntityType<?> entityType, final String id) {
		EntityType<EntityType> metaType = BeanLocator.getInstance().getInstance(EntityTypeRepository.class)
				.getEntityType(EntityType.class);
		final FindByIdService findByIdService = entityType.getService(FindByIdService.class);

		Comparison comparison = createComparison(entityType);
		EntityObserverDefinition entityObserverDefinition = entityObserverFactory.createDefinition(comparison);
		EntityObserver entityObserver = entityObserverDefinition.create();
		entityObserver.setHandle(new EntityHandle() {

			@Override
			public Object getEntity() {
				Object entity = findByIdService.findById(entityType, id);
				return entity;
			}
		});
		return entityObserver;
	}

	public ComparisonBuilderFactory getComparisonBuilderFactory() {
		return comparisonBuilderFactory;
	}

	public EntityObserverFactory getEntityObserverFactory() {
		return entityObserverFactory;
	}

	private SingleAttribute<DerivedType> getOriginalTypeAttribut() {
		return (SingleAttribute<DerivedType>) BeanLocator.getInstance().getInstance(EntityTypeRepository.class)
				.getEntityType(EntityType.class).getMetaAttribute(DerivedObject.META_ATTRIBUTE_CODE);
	}

	@Override
	public String getScope(EntityType<?> type, String id) {
		EntityType<?> originalType = getOriginalTypeAttribut().getValue(type).getOriginalType();
		ObservationService observationService = originalType.getService(ObservationService.class);
		return observationService.getScope(originalType, id);
	}

	@Override
	public boolean isObservable(EntityType<?> type, String id) {
		return true;
	}

	public void setComparisonBuilderFactory(ComparisonBuilderFactory comparisonBuilderFactory) {
		this.comparisonBuilderFactory = comparisonBuilderFactory;
	}

	public void setEntityObserverFactory(EntityObserverFactory entityObserverFactory) {
		this.entityObserverFactory = entityObserverFactory;
	}
}
