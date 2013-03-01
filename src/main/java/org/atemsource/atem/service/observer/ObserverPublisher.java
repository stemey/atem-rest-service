package org.atemsource.atem.service.observer;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.annotation.PreDestroy;
import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.entity.ObservationService;
import org.atemsource.atem.utility.compare.AttributeChange;
import org.atemsource.atem.utility.compare.Difference;
import org.atemsource.atem.utility.observer.AttributeListener;
import org.atemsource.atem.utility.observer.EntityObserver;
import org.atemsource.atem.utility.observer.WatchHandle;
import org.cometd.bayeux.client.ClientSession;
import org.cometd.bayeux.client.ClientSessionChannel;
import org.cometd.bayeux.client.ClientSessionChannel.MessageListener;
import org.cometd.bayeux.server.BayeuxServer;
import org.cometd.bayeux.server.ConfigurableServerChannel;
import org.cometd.bayeux.server.ServerChannel;
import org.cometd.bayeux.server.ServerChannel.SubscriptionListener;
import org.cometd.bayeux.server.ServerSession;
import org.eclipse.jetty.util.ConcurrentHashSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class ObserverPublisher {
	private BayeuxServer bayeuxServer;
	private Map<String, Set<Observation>> observationsForSession = new ConcurrentHashMap<String, Set<Observation>>();
	
	protected static Logger logger = LoggerFactory.getLogger(ObserverPublisher.class);

	private Pattern topicPattern = Pattern.compile("/entity/observation/([^/]+)/([^/]+)/([^/]+)");
	@Inject
	private EntityTypeRepository entityTypeRepository;
	
	public void initialize() {

		clientSession = bayeuxServer.newLocalSession("entity");
		clientSession.handshake();
			org.cometd.bayeux.server.BayeuxServer.SubscriptionListener subscriptionListener = new org.cometd.bayeux.server.BayeuxServer.SubscriptionListener() {

				public void subscribed(ServerSession session, ServerChannel channel) {
					try {
					onSubscription(channel);
					} catch (Exception e) {
						logger.error("error on subscription of channel "+channel.getId(),e);
					}
				}

				public void unsubscribed(ServerSession session, ServerChannel channel) {
					try {
						endSupscription(channel);
					} catch (Exception e) {
						logger.error("error on subscription of channel "+channel.getId(),e);
					}

				}
			};
			bayeuxServer.addListener(subscriptionListener);
		}

	

	@PreDestroy
	public void destroy() throws Exception {
		clientSession.disconnect();
	}
	private Observation findObservation(String channelName) {
		for (Set<Observation> observations:observationsForSession.values()) {
			Observation observation = getObservation(channelName, observations);
			if (observation!=null) {
				return observation;
			}
		}
		return null;
	}

	
	public boolean isObserved(String sessionId,String type, String id) {
		Observation observation = findObservation(getChannelname(sessionId, type, id));
		if (observation==null) {
			return false;
		}else{
			return observation.isSubscribed();
		}
	}


//	public String addObservable(String sessionId,String type, final String name, EntityObserver entityObserver) {
//		String channelName = getChannelname(sessionId,type, name);
//		final Observation observation = new Observation();
//		observation.setEntityObserver(entityObserver);
//		observation.setChannelName(channelName);
//		 addObservable(sessionId,  observation);
//		 return channelName;
//	}

	protected void addObservable(String sessionId, String scope,
			final Observation observation) {
		addObservation(sessionId, observation);

		WatchHandle watchHandle = observation.watch(new AttributeListener() {

			public boolean onEvent(Difference difference) {
				if (difference instanceof AttributeChange) {
					AttributeChange change= (AttributeChange) difference;
					Map<String,Object> diff= new HashMap<String, Object>();
					diff.put("path",change.getPath());
					diff.put("oldValue",change.getOldValue());
					diff.put("newValue",change.getNewValue());
					clientSession.getChannel(observation.getChannelName()).publish(diff);
				}
				return true;
			}
		});
		observation.setWatchHandle(watchHandle);

	}

	private void addObservation(String sessionId, Observation observation) {
		Set<Observation> set = observationsForSession.get(sessionId);
		if(set==null) {
			set = new HashSet<Observation>();
			observationsForSession.put(sessionId, set);
		}
		set.add(observation);
	}

	protected String getChannelname(String sessionid,String type, final String id) {
		return "/entity/observation/"+sessionid+"/"+type+"/"+id;
	}

	private ClientSession clientSession;

	public void check(String sessionId) {
		Set<Observation> observations = observationsForSession.get(sessionId);
		if (observations != null) {
			for (Observation observation : observations) {
				if (observation.isSubscribed()) {
					observation.check();
				}
			}
		}
	}

	public void closeSession(String sessionId) {
		Set<Observation> observations = observationsForSession.get("sessionId");
		for (Observation observation : observations) {
			if (observation.isSubscribed()) {
				clientSession.getChannel(observation.getChannelName()).unsubscribe();
			}
			observations.remove(observation.getChannelName());
		}
		observationsForSession.remove("sessionId");
	}
	
	private Observation getObservation(String session,String  channelName) {
		return getObservation(channelName,observationsForSession.get(session));
	}

	private Observation getObservation(String channelName, Set<Observation> observations) {
		if (observations==null) {
			return null;
		}else{
			for(Observation observation:observations) {
				if (observation.getChannelName().equals(channelName)) {
					return observation;
				}
			}
		}
		return null;
	}

	public void bayeuxServerStarted(BayeuxServer bayeuxServer) {
		this.bayeuxServer = bayeuxServer;
		initialize();
	}

	public void removeObservables(String sessionId) {
		observationsForSession.remove(sessionId);
	}



	public String getChannelPattern(String scope,String code, String id) {
		return "/entity/observation/"+scope+"/"+code+"/"+id;
	}



	protected void onSubscription(ServerChannel channel) {
		String channelName=channel.getId();
		Matcher matcher = topicPattern.matcher(channelName);
		if (matcher.find()) {
			String sessionId=matcher.group(1);
			String typeCode=matcher.group(2);
			String id=matcher.group(3);
			if (!isObserved(sessionId, typeCode, id)) {
			EntityType<Object> entityType = entityTypeRepository.getEntityType(typeCode);
			ObservationService observationService = entityType.getService(ObservationService.class);
			EntityObserver entityObserver = observationService.createObserver(entityType, id);
			channel.setPersistent(true);
			Observation observation=new Observation();
			observation.setChannelName(channel.getId());
			observation.setEntityObserver(entityObserver);
			observation.addSubscriber();
			addObservable(sessionId,observationService.getScope(entityType,id), observation);
			}else{
				Observation observation = findObservation(channelName);
				observation.addSubscriber();
			}
		}
	}



	protected void endSupscription(ServerChannel channel) {
		Observation observation = findObservation(channel.getId());
		if (observation != null) {
			observation.removeSubscriber();
		}
	}

}
