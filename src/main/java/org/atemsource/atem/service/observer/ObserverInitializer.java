package org.atemsource.atem.service.observer;

import java.util.Set;

import javax.annotation.PostConstruct;
import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.entity.ObservationService;
import org.atemsource.atem.utility.observer.EntityObserver;
import org.springframework.stereotype.Component;



@Component
public class ObserverInitializer {
	@Inject
	private ObserverPublisher observerPublisher;

	public Set<ObservationDefinition> getApplicationObserverationDefinitions() {
		return applicationObserverationDefinitions;
	}

	public void setApplicationObserverationDefinitions(Set<ObservationDefinition> applicationObserverationDefinitions) {
		this.applicationObserverationDefinitions = applicationObserverationDefinitions;
	}

	public Set<ObservationDefinition> getSessionObserverationDefinitions() {
		return sessionObserverationDefinitions;
	}

	public void setSessionObserverationDefinitions(Set<ObservationDefinition> sessionObserverationDefinitions) {
		this.sessionObserverationDefinitions = sessionObserverationDefinitions;
	}

	Set<ObservationDefinition> applicationObserverationDefinitions;
	Set<ObservationDefinition> sessionObserverationDefinitions;

//	@PostConstruct
//	public void initialize() {
//
//		for (ObservationDefinition observationDefinition : applicationObserverationDefinitions) {
//			EntityObserver entityObserver = observationDefinition.createObserver();
//			observerPublisher.addObservable("application", observationDefinition.getName(), entityObserver);
//		}
//		observerPublisher.check("application");
//
//	}

//	public void startSession(String id) {
//		for (ObservationDefinition observationDefinition : applicationObserverationDefinitions) {
//			EntityObserver entityObserver = observationDefinition.createObserver();
//			observerPublisher.addObservable(id, observationDefinition.getName(), entityObserver);
//		}
//		observerPublisher.check(id);
//	}
//
//	public void endSession(String id) {
//		observerPublisher.removeObservables(id);
//	}
//	

//	public String getObservableTopic(EntityType<?> type, String id) {
//		ObservationService observationService = type.getService(ObservationService.class);
//		if(observationService==null) {
//			return null;
//		}else{
//			String session=observationService.getScope(type,id);
//			
//		}
//		for (ObservationDefinition observationDefinition:sessionObserverationDefinitions) {
//			if (observationDefinition.manages(type,id)) {
//				return observerPublisher.getChannelname("session", observationDefinition.getName());
//			}
//		}
//		for (ObservationDefinition observationDefinition:applicationObserverationDefinitions) {
//			if (observationDefinition.manages(type,id)) {
//				return observerPublisher.getChannelname("application", observationDefinition.getName());
//			}
//		}
//		return null;
//	}

}
