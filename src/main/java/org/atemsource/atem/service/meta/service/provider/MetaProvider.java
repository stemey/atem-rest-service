package org.atemsource.atem.service.meta.service.provider;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.meta.service.model.Service;
import org.codehaus.jackson.node.ObjectNode;

public class MetaProvider {

	private List<ServiceProvider<Service>> serviceProviders;

	public <T> ObjectNode getGformSchema(EntityType<T> entityType) {
		ObjectNode schema = null;
		Iterator<ServiceProvider<Service>> iterator = serviceProviders
				.iterator();
		while (iterator.hasNext() && schema == null) {
			ServiceProvider<Service> provider = iterator.next();

			if (provider.handles(entityType)) {
				schema = provider.getSchema(entityType);
			}
		}
		return schema;
	}

	public void setServiceProviders(
			List<ServiceProvider<Service>> serviceProviders) {
		this.serviceProviders = serviceProviders;
	}

	public Category getCategory(String baseUri) {
		Category category = new Category();
		List<Service> resources = new ArrayList<Service>();
		for (ServiceProvider<Service> provider : serviceProviders) {
			ServiceGroup group = new ServiceGroup();
			category.add(group);
			group.setServices(provider.getServices());
			resources.addAll(provider.getServices());
		}
		category.setResources(resources);
		category.setBasePath(baseUri);
		return category;
	}

	

}
