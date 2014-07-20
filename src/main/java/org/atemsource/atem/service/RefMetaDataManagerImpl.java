package org.atemsource.atem.service;

import java.io.Serializable;
import java.util.Iterator;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.service.IdentityAttributeService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.meta.service.provider.TransformationFactory;
import org.atemsource.atem.service.meta.service.provider.resource.SchemaRefResolver;
import org.atemsource.atem.service.refresolver.RefResolver;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;

public class RefMetaDataManagerImpl implements RefMetaDataManager {

	private EntityTypeRepository entityTypeRepository;

	private RefResolver refResolver;

	private SchemaRefResolver schemaRefResolver;

	public EntityTypeTransformation<?, ?> getTransformation(
			EntityType<?> jsonTargetType) {
		DerivedType derivedType = (DerivedType) jsonTargetType.getMetaType()
				.getMetaAttribute(DerivedType.META_ATTRIBUTE_CODE)
				.getValue(jsonTargetType);
		return derivedType.getTransformation();
	}

	@Override
	public String getIdAttribute(String originalTargetTypeCode) {
		EntityType<?> targetType = entityTypeRepository
				.getEntityType(originalTargetTypeCode);
		EntityTypeTransformation<?, ?> transformation = getTransformation(targetType);
		SingleAttribute<? extends Serializable> idAttribute = transformation
				.getEntityTypeA().getService(IdentityAttributeService.class)
				.getIdAttribute(targetType);
		Attribute derivedIdAttribute = transformation
				.getDerivedAttribute(idAttribute);
		return derivedIdAttribute.getCode();
	}

	@Override
	public String getSearchAttribute(String originalTargetTypeCode) {
		EntityType<?> targetType = entityTypeRepository
				.getEntityType(originalTargetTypeCode);
		EntityTypeTransformation<?, ?> transformation = getTransformation(targetType);
		SingleAttribute<String> searchAttribute = null;
		Iterator<Attribute> iterator = transformation.getEntityTypeB()
				.getAttributes().iterator();
		while (iterator.hasNext() && searchAttribute == null) {
			Attribute attribute = iterator.next();
			if (attribute instanceof SingleAttribute
					&& attribute.getTargetType().getJavaType() == String.class) {
				searchAttribute = (SingleAttribute<String>) attribute;
			}
		}
		if (searchAttribute == null) {
			return null;
		} else {
			return transformation.getDerivedAttribute(searchAttribute)
					.getCode();
		}
	}

	@Override
	public String getSearchUri(String originalTargetTypeCode) {
		EntityType<?> targetType = entityTypeRepository
				.getEntityType(originalTargetTypeCode);
		EntityTypeTransformation<?, ?> transformation = getTransformation(targetType);
		return refResolver.getCollectionUri(transformation.getEntityTypeB());
	}

	public void setEntityTypeRepository(
			EntityTypeRepository entityTypeRepository) {
		this.entityTypeRepository = entityTypeRepository;
	}

	public void setRefResolver(RefResolver refResolver) {
		this.refResolver = refResolver;
	}

	public void setSchemaRefResolver(SchemaRefResolver schemaRefResolver) {
		this.schemaRefResolver = schemaRefResolver;
	}

	@Override
	public String getSchemaUri(String originalTargetTypeCode) {
		EntityType<?> targetType = entityTypeRepository
				.getEntityType(originalTargetTypeCode);
		EntityTypeTransformation<?, ?> transformation = getTransformation(targetType);
		return schemaRefResolver.getSchemaUri(transformation.getEntityTypeB());
	}

}
