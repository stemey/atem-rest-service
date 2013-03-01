package org.atemsource.atem.service.observer;

import java.lang.reflect.Field;

import junit.framework.Assert;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.entity.ObservationService;
import org.atemsource.atem.utility.observer.EntityObserver;
import org.cometd.bayeux.server.ServerChannel;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Test;

public class ObserverPublisherTest {

	
	ObserverPublisher observerPublisher= new ObserverPublisher();
	   Mockery context = new Mockery();
	
	@Test
	public void test() throws NoSuchFieldException, SecurityException {
		final String type = "myType";
		final String sessionid = "xxx";
		final String id = "1";
		final String channelname = observerPublisher.getChannelname(sessionid, type, id);
		final ServerChannel serverChannel = context.mock(ServerChannel.class);
		final EntityTypeRepository entityTypeRepository = context.mock(EntityTypeRepository.class);
		final EntityType<?> entityType = context.mock(EntityType.class);
		final ObservationService observationService = context.mock(ObservationService.class);
		final EntityObserver entityObserver = new EntityObserver();
		
		setField("entityTypeRepository", observerPublisher, entityTypeRepository);
		
		context.checking(new Expectations(){{
			allowing(serverChannel).getId();
			will(returnValue(channelname));
			oneOf(serverChannel).setPersistent(true);

			oneOf(entityTypeRepository).getEntityType(type);
			will(returnValue(entityType));
			oneOf(entityType).getService(ObservationService.class);
			will(returnValue(observationService));
			oneOf(entityType).getService(ObservationService.class);
			will(returnValue(observationService));
			oneOf(observationService).createObserver(entityType, id);
			will(returnValue(entityObserver));
			
			oneOf(observationService).getScope(entityType, id);
			will(returnValue(ObservationService.SESSION));
			
		}});
		observerPublisher.onSubscription(serverChannel);
		
		Assert.assertTrue(observerPublisher.isObserved(sessionid, type, id));
		observerPublisher.endSupscription(serverChannel);
		Assert.assertFalse(observerPublisher.isObserved(sessionid, type, id));
		
	}

	private void setField(String fieldName, Object target,Object value) {
		Field declaredField;
		try {
			declaredField = target.getClass().getDeclaredField(fieldName);
			declaredField.setAccessible(true);
			declaredField.set(target, value);
		} catch (NoSuchFieldException e) {
			throw new RuntimeException("cannot set value on field "+fieldName,e);
		} catch (SecurityException e) {
			throw new RuntimeException("cannot set value on field "+fieldName,e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("cannot set value on field "+fieldName,e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("cannot set value on field "+fieldName,e);
		}
		
	}

}
