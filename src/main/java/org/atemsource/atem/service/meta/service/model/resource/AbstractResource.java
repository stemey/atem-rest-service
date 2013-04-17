package org.atemsource.atem.service.meta.service.model.resource;

import java.util.Set;

import org.atemsource.atem.api.attribute.annotation.Association;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.meta.service.binding.EditorConversion;
import org.atemsource.atem.service.meta.service.model.Service;
import org.atemsource.atem.utility.transform.api.annotation.Conversion;

public class AbstractResource extends Service {

	//@Conversion(DojoTableConversion.class)
	private EntityType<?> collectionType;
	@Conversion(EditorConversion.class)
	private EntityType<?> resourceType;
	private String idAttribute;
	@Association(targetType = ResourceOperation.class)
	private Set<ResourceOperation> singleOperations;
	private String uriPath;

	public EntityType<?> getResourceType() {
		return resourceType;
	}
	
	private String topic;

	public String getTopic() {
		return topic;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public void setResourceType(EntityType<?> resourceType) {
		this.resourceType = resourceType;
	}

	public Set<ResourceOperation> getSingleOperations() {
		return singleOperations;
	}

	public void setSingleOperations(Set<ResourceOperation> singleResourceOperations) {
		this.singleOperations = singleResourceOperations;
	}

	public String getUriPath() {
		return uriPath;
	}

	public void setUriPath(String uriPath) {
		this.uriPath = uriPath;
	}

	public AbstractResource() {
		super();
	}

	public boolean isRequest(String requestUrl) {
		return requestUrl.startsWith(uriPath);
	}

}