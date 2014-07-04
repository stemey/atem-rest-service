package org.atemsource.atem.service.meta.service.provider;

import java.util.List;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.meta.service.model.Service;
import org.codehaus.jackson.node.ObjectNode;

public interface ServiceProvider<S extends Service> {

	List<S> getServices();
	
	public <O> boolean handles(EntityType<O> entityType);
	
	public <O> ObjectNode getSchema(EntityType<O> entityType);
}
