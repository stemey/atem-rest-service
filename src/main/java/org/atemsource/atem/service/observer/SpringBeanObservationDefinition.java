package org.atemsource.atem.service.observer;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.infrastructure.bean.Bean;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.view.View;
import org.atemsource.atem.api.view.ViewVisitor;
import org.atemsource.atem.api.view.Visitor;
import org.atemsource.atem.utility.compare.Comparison;
import org.atemsource.atem.utility.compare.ComparisonBuilder;
import org.atemsource.atem.utility.compare.ComparisonBuilderFactory;
import org.atemsource.atem.utility.observer.EntityHandle;
import org.atemsource.atem.utility.observer.EntityObserver;
import org.atemsource.atem.utility.observer.EntityObserverDefinition;
import org.atemsource.atem.utility.observer.EntityObserverFactory;

public class SpringBeanObservationDefinition implements ObservationDefinition {

	private String beanId;

	@Inject
	private BeanLocator beanLocator;
	@Inject
	private EntityObserverFactory entityObserverFactory;
	@Inject
	private ComparisonBuilderFactory comparisonBuilderFactory;
	@Inject
	private EntityTypeRepository entityTypeRepository;


	public String getBeanId() {
		return beanId;
	}

	public void setBeanId(String beanId) {
		this.beanId = beanId;
	}

	
	@Override
	public EntityObserver createObserver() {

		EntityObserver entityObserver = entityObserverDefinition.create();
		entityObserver.setHandle(new EntityHandle() {

			public Object getEntity() {
				return bean.get();
			}
		});
		return entityObserver;
	}

	private Bean<?> bean;

	@PostConstruct
	public void initialize() {
		bean = beanLocator.getBean(beanId);
		EntityType<?> entityType = entityTypeRepository.getEntityType(bean.getBeanClass());
		Comparison comparison = createComparison(entityType);
		entityObserverDefinition = entityObserverFactory.createDefinition(comparison);
	}

	private EntityObserverDefinition entityObserverDefinition;

	@Override
	public String getName() {
		return "spring:"+beanId;
	}

	protected Comparison createComparison(EntityType<?> entityType) {
		ComparisonBuilder comparisonBuilder = comparisonBuilderFactory.create(entityType);

		entityType.visit(new ViewVisitor<ComparisonBuilder>() {

			public void visit(ComparisonBuilder context, Attribute attribute) {
				context.include(attribute);
			}

			public void visit(ComparisonBuilder context, Attribute attribute,
					Visitor<ComparisonBuilder> targetTypeVisitor) {
				if (((JavaMetaData) attribute).getAnnotation(Inject.class) == null) {
					ComparisonBuilder cascade = context.include(attribute).cascade();
					targetTypeVisitor.visit(cascade);
				}
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

	@Override
	public boolean manages(EntityType<?> originalType, String id) {
		return id.equals(beanId) && originalType.getJavaType().isAssignableFrom(bean.getBeanClass());
	}
}
