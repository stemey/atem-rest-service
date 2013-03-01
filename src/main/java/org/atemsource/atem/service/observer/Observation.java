package org.atemsource.atem.service.observer;

import java.util.concurrent.atomic.AtomicInteger;

import org.atemsource.atem.utility.observer.AttributeListener;
import org.atemsource.atem.utility.observer.EntityObserver;
import org.atemsource.atem.utility.observer.WatchHandle;

public class Observation {
	private EntityObserver entityObserver;
	private String channelName;
	private AtomicInteger subscriptionCount=new AtomicInteger();
	private WatchHandle watchHandle;
	public WatchHandle getWatchHandle() {
		return watchHandle;
	}
	public void setWatchHandle(WatchHandle watchHandle) {
		this.watchHandle = watchHandle;
	}
	public EntityObserver getEntityObserver() {
		return entityObserver;
	}
	public void setEntityObserver(EntityObserver entityObserver) {
		this.entityObserver = entityObserver;
	}
	public String getChannelName() {
		return channelName;
	}
	public void setChannelName(String channelName) {
		this.channelName = channelName;
	}
	public boolean isSubscribed() {
		return subscriptionCount.get()>0;
	}
	public void addSubscriber()
	{
		this.subscriptionCount.incrementAndGet();
	}
	public void removeSubscriber()
	{
		this.subscriptionCount.decrementAndGet();
	}
	public WatchHandle watch(AttributeListener attributeListener) {
		return entityObserver.watch(attributeListener);
	}
	public void check() {
		entityObserver.check();
	}
}
