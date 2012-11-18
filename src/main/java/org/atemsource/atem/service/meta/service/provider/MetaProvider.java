package org.atemsource.atem.service.meta.service.provider;

import java.util.List;

import javax.annotation.PostConstruct;

import org.atemsource.atem.service.meta.service.model.Meta;
import org.atemsource.atem.service.meta.service.model.Service;

public class MetaProvider {
	private List<ServiceProvider<Service>> serviceProviders;
	private Meta meta;

	@PostConstruct
	public void initialize() {
		meta = new Meta();
		for (ServiceProvider<Service> serviceProvider : serviceProviders) {
			meta.addServices(serviceProvider.getServices());
		}
	}

	public void setServiceProviders(List<ServiceProvider<Service>> serviceProviders) {
		this.serviceProviders = serviceProviders;
	}

	public Meta getMeta() {
		return meta;
	}
}
