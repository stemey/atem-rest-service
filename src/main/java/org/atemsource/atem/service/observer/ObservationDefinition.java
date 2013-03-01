package org.atemsource.atem.service.observer;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.observer.EntityObserver;
import org.atemsource.atem.utility.observer.EntityObserverDefinition;

public interface ObservationDefinition {
	public EntityObserver createObserver();
	public String getName();
	public boolean manages(EntityType<?> originalType, String id);

}
