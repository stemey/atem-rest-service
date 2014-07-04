package org.atemsource.atem.service.meta.service.model;

import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.atemsource.atem.api.attribute.annotation.MapAssociation;

public class Meta {

	private static final long serialVersionUID = 1L;
	private String basePath;

	@MapAssociation(keyType = String.class, targetType = Service.class)
	private final List<Service> resources = new LinkedList< Service>();

	public String getBasePath() {
		return basePath;
	}

	public void setBasePath(String basePath) {
		this.basePath = basePath;
	}

	public List<Service> getResources() {
		return resources;
	}

	public Meta() {
	}

	public void addService(Service method) {
		resources.add(method);
	}


	public void addServices(Set<? extends Service> services) {
		for (Service service : services) {
			addService(service);
		}
	}

}
