package org.atemsource.atem.service.meta.service.provider;

import java.util.Iterator;
import java.util.List;

import javax.annotation.PostConstruct;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.gform.GformContext;
import org.atemsource.atem.service.meta.service.model.Meta;
import org.atemsource.atem.service.meta.service.model.Service;
import org.codehaus.jackson.node.ObjectNode;

public class MetaProvider {
	private GformContext gformContext;

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

	public void setGformContext(GformContext gformContext) {
		this.gformContext = gformContext;
	}

	public void setServiceProviders(
			List<ServiceProvider<Service>> serviceProviders) {
		this.serviceProviders = serviceProviders;
	}

	public Category getCategory() {
		Category category = new Category();
		for (ServiceProvider<Service> provider : serviceProviders) {
			ServiceGroup group = new ServiceGroup();
			category.add(group);
			group.setServices(provider.getServices());
		}
		return category;
	}

}
