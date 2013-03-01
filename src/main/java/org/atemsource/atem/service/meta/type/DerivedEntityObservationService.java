package org.atemsource.atem.service.meta.type;

import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.api.view.Visitor;
import org.atemsource.atem.impl.meta.DerivedObject;
import org.atemsource.atem.service.entity.CrudService;
import org.atemsource.atem.service.entity.ObservationService;
import org.atemsource.atem.utility.compare.Comparison;
import org.atemsource.atem.utility.compare.ComparisonAttributeBuilderFactory;
import org.atemsource.atem.utility.compare.ComparisonBuilder;
import org.atemsource.atem.utility.compare.ComparisonBuilderFactory;
import org.atemsource.atem.utility.observer.EntityHandle;
import org.atemsource.atem.utility.observer.EntityObserver;
import org.atemsource.atem.utility.observer.EntityObserverDefinition;
import org.atemsource.atem.utility.observer.EntityObserverFactory;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.springframework.context.ApplicationContext;

public class DerivedEntityObservationService implements ObservationService {
	
	
	public ComparisonBuilderFactory getComparisonBuilderFactory() {
		return comparisonBuilderFactory;
	}

	public void setComparisonBuilderFactory(ComparisonBuilderFactory comparisonBuilderFactory) {
		this.comparisonBuilderFactory = comparisonBuilderFactory;
	}

	public EntityObserverFactory getEntityObserverFactory() {
		return entityObserverFactory;
	}

	public void setEntityObserverFactory(EntityObserverFactory entityObserverFactory) {
		this.entityObserverFactory = entityObserverFactory;
	}

	private ComparisonBuilderFactory comparisonBuilderFactory;
	
	
	private EntityObserverFactory entityObserverFactory;

	private SingleAttribute<DerivedType> getOriginalTypeAttribut() {
		return (SingleAttribute<DerivedType>) BeanLocator.getInstance().getInstance(EntityTypeRepository.class).getEntityType(EntityType.class).getMetaAttribute(DerivedObject.META_ATTRIBUTE_CODE);
	}
	
	protected Comparison createComparison(EntityType<?> entityType) {
		ComparisonBuilder comparisonBuilder = comparisonBuilderFactory.create(entityType);

		entityType.visit(new ViewVisitor<ComparisonBuilder>() {

			public void visit(ComparisonBuilder context, Attribute attribute) {
				context.include(attribute);
			}

			public void visit(ComparisonBuilder context, Attribute attribute,
					Visitor<ComparisonBuilder> targetTypeVisitor) {
					ComparisonBuilder cascade = context.include(attribute).cascade();
					targetTypeVisitor.visit(cascade);
			}

			public boolean visitSubView(ComparisonBuilder context, View view) {
				return false;
			}

			public boolean visitSuperView(ComparisonBuilder context, View view) {
				return false;
			}
		}, comparisonBuilder);
		return comparisonBuilder.create();
	}

	public EntityObserver createObserver(final EntityType<?> entityType, final String id) {
		EntityType<EntityType> metaType = BeanLocator.getInstance().getInstance(EntityTypeRepository.class).getEntityType(EntityType.class);
		final CrudService crudService = entityType.getService(CrudService.class);

		Comparison comparison = createComparison(entityType);
		EntityObserverDefinition entityObserverDefinition = entityObserverFactory.createDefinition(comparison);
		 EntityObserver entityObserver = entityObserverDefinition.create();
		 entityObserver.setHandle(new EntityHandle() {
			
			@Override
			public Object getEntity() {
				Object entity = crudService.findEntity(entityType, id);
				return entity;
			}
		});
		return entityObserver;
	}

	@Override
	public String getScope(EntityType<?> type,String id) {
		EntityType<?> originalType = getOriginalTypeAttribut().getValue(type).getOriginalType();
		ObservationService observationService = originalType.getService(ObservationService.class);
		return observationService.getScope(originalType,id);
	}

	@Override
	public boolean isObservable(EntityType<?> type, String id) {
		return true;
	}
}
