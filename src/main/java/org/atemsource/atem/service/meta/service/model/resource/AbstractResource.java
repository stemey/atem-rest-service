package org.atemsource.atem.service.meta.service.model.resource;

import java.util.Set;

import org.atemsource.atem.api.attribute.annotation.Association;
import org.atemsource.atem.service.meta.service.model.Service;
import org.atemsource.atem.utility.binding.jackson.NodeConversion;
import org.atemsource.atem.utility.transform.api.annotation.Conversion;
import org.codehaus.jackson.node.ObjectNode;

public class AbstractResource extends Service {

	@Conversion(NodeConversion.class)
	private ObjectNode resourceType;

	@Association(targetType = ResourceOperation.class)
	private Set<ResourceOperation> singleOperations;

	private String topic;

	private String uriPath;

	public AbstractResource() {
		super();
	}

	public ObjectNode getResourceType() {
		return resourceType;
	}

	public Set<ResourceOperation> getSingleOperations() {
		return singleOperations;
	}

	public String getTopic() {
		return topic;
	}

	public String getUriPath() {
		return uriPath;
	}

	public boolean isRequest(String requestUrl) {
		return requestUrl.startsWith(uriPath);
	}

	public void setResourceType(ObjectNode resourceType) {
		this.resourceType = resourceType;
	}

	public void setSingleOperations(
			Set<ResourceOperation> singleResourceOperations) {
		this.singleOperations = singleResourceOperations;
	}

	public void setTopic(String topic) {
		this.topic = topic;
	}

	public void setUriPath(String uriPath) {
		this.uriPath = uriPath;
	}

}