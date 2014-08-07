package org.atemsource.atem.service.gform.type;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.primitive.RefType;
import org.atemsource.atem.service.RefMetaDataManager;
import org.atemsource.atem.service.gform.TypeBuilder;
import org.atemsource.atem.service.gform.TypeCreator;

public class ReferenceTypeCreator extends TypeCreator {

	private RefMetaDataManager refMetaDataManager;

	@Override
	public void create(TypeBuilder typeBuilder, Attribute<?, ?> attribute) {
		typeBuilder.type("ref");
		RefType<?> refType = (RefType<?>) attribute.getTargetType();
		if (refType.getTargetTypes().length!=1) {
			throw new IllegalArgumentException("cannot handle ref type with more than one target type");
		}
		
		EntityType<?> targetType = refType.getTargetTypes()[0];

		typeBuilder.getNode().put("schemaUrl",
				refMetaDataManager.getSchemaUri(targetType.getCode()));
		typeBuilder.getNode().put("url",
				refMetaDataManager.getSearchUri(targetType.getCode()));

		typeBuilder.getNode().put("idProperty",
				refMetaDataManager.getIdAttribute(targetType.getCode()));
		typeBuilder.getNode().put("searchProperty",
				refMetaDataManager.getSearchAttribute(targetType.getCode()));

		super.create(typeBuilder, attribute);
	}

	public void setRefMetaDataManager(RefMetaDataManager refMetaDataManager) {
		this.refMetaDataManager = refMetaDataManager;
	}

	@Override
	public boolean handles(Attribute<?, ?> attribute) {
		return  attribute.getTargetType() instanceof RefType;
	}

}
