package org.atemsource.atem.service.entity;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.observer.ObservationDefinition;
import org.atemsource.atem.utility.observer.EntityObserver;

public interface ObservationService {
	public EntityObserver createObserver(EntityType<?> type,String id);
	public boolean isObservable(EntityType<?> type,String id);

	String getScope(EntityType<?> type,String id);
	
	public static final String APPLICATION="application";
	public static final String SESSION="session";

}
