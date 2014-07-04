package org.atemsource.atem.service.meta.service.provider;

import java.util.List;

import org.atemsource.atem.service.meta.service.model.Service;

public class ServiceGroup extends Group{

	private List<Service> services;

	public List<Service> getServices() {
		return services;
	}

	public void setServices(List<Service> services) {
		this.services = services;
	}
}
