package org.atemsource.atem.service.meta.service.model.resource;

import java.util.Set;

import org.atemsource.atem.api.attribute.annotation.Association;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.meta.service.binding.EditorConversion;
import org.atemsource.atem.service.meta.service.model.Service;
import org.atemsource.atem.utility.transform.api.annotation.Conversion;


public class AbstractResource extends Service
{

	@Conversion(EditorConversion.class)
	private EntityType<?> resourceType;

	@Association(targetType = ResourceOperation.class)
	private Set<ResourceOperation> singleOperations;

	private String topic;

	private String uriPath;

	public AbstractResource()
	{
		super();
	}

	public EntityType<?> getResourceType()
	{
		return resourceType;
	}

	public Set<ResourceOperation> getSingleOperations()
	{
		return singleOperations;
	}

	public String getTopic()
	{
		return topic;
	}

	public String getUriPath()
	{
		return uriPath;
	}

	public boolean isRequest(String requestUrl)
	{
		return requestUrl.startsWith(uriPath);
	}

	public void setResourceType(EntityType<?> resourceType)
	{
		this.resourceType = resourceType;
	}

	public void setSingleOperations(Set<ResourceOperation> singleResourceOperations)
	{
		this.singleOperations = singleResourceOperations;
	}

	public void setTopic(String topic)
	{
		this.topic = topic;
	}

	public void setUriPath(String uriPath)
	{
		this.uriPath = uriPath;
	}

}