package org.atemsource.atem.service.meta.type;

import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.entity.ObservationService;
import org.atemsource.atem.utility.observer.EntityObserver;
import org.springframework.context.annotation.Scope;

public class SpringBeanObservationService implements ObservationService {

	@Override
	public EntityObserver createObserver(EntityType<?> type, String id) {
		return null;
	}

	public boolean isObservable(EntityType<?> type, String id) {
		return false;
	}

	@Override
	public String getScope(EntityType<?> type, String id) {
		Scope scope = 	type.getJavaType().getAnnotation(Scope.class);
		if (scope == null) {
			return APPLICATION;
		} else {
			return "{"+scope.value()+"}";
		}
	}

}
