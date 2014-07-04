package org.atemsource.atem.service.meta.service.model.resource;

import java.util.Set;

import org.atemsource.atem.api.attribute.annotation.Association;
import org.atemsource.atem.service.meta.service.model.Service;

public class AbstractResource extends Service {


	@Association(targetType = ResourceOperation.class)
	private Set<ResourceOperation> singleOperations;

	private String topic;

	private String uriPath;

	public AbstractResource() {
		super();
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