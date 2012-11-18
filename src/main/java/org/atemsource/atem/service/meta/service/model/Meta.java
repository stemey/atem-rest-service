package org.atemsource.atem.service.meta.service.model;

import java.util.Map;
import java.util.Set;
import java.util.SortedMap;
import java.util.TreeMap;

import org.atemsource.atem.api.attribute.annotation.MapAssociation;
import org.atemsource.atem.service.meta.service.model.method.Method;
import org.atemsource.atem.service.meta.service.provider.ServiceProvider;

public class Meta {

	private static final long serialVersionUID = 1L;

	@MapAssociation(keyType = String.class, targetType = Method.class)
	private final SortedMap<String, Service> methods = new TreeMap<String, Service>();

	public Meta() {
	}

	public void addService(Service method) {
		methods.put(method.getName(), method);
	}

	public Map<String, Service> getServices() {
		return methods;
	}

	public void addServices(Set<? extends Service> services) {
		for (Service service : services) {
			addService(service);
		}
	}

}
