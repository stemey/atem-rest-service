package org.atemsource.atem.service.meta.service.model.resource;

import java.util.Set;

import org.atemsource.atem.api.attribute.annotation.Association;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.meta.service.binding.EditorConversion;
import org.atemsource.atem.utility.transform.api.annotation.Conversion;


public class Resource extends AbstractResource
{
	@Association(targetType = CollectionOperation.class)
	private Set<CollectionOperation> collectionOperations;

	private String idProperty;

	@Conversion(EditorConversion.class)
	private EntityType<?> singleResourceType;

	public Set<CollectionOperation> getCollectionOperations()
	{
		return collectionOperations;
	}

	public String getIdProperty()
	{
		return idProperty;
	}

	public EntityType<?> getSingleResourceType()
	{
		return singleResourceType;
	}

	public void setCollectionOperations(Set<CollectionOperation> collectionSupportedOperations)
	{
		this.collectionOperations = collectionSupportedOperations;
	}

	public void setIdProperty(String idProperty)
	{
		this.idProperty = idProperty;
	}

	public void setSingleResourceType(EntityType<?> singleResourceType)
	{
		this.singleResourceType = singleResourceType;
	}

}
