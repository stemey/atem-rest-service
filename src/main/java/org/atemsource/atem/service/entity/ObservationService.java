package org.atemsource.atem.service.entity;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.observer.EntityObserver;


public interface ObservationService
{
	public static final String APPLICATION = "application";

	public static final String SESSION = "session";

	EntityObserver createObserver(EntityType<?> type, String id);

	boolean isObservable(EntityType<?> type, String id);

	String getScope(EntityType<?> type, String id);

}
