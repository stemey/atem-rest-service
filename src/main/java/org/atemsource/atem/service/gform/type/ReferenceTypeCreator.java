package org.atemsource.atem.service.gform.type;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.primitive.RefType;
import org.atemsource.atem.service.RefMetaDataManager;
import org.atemsource.atem.service.gform.TypeBuilder;
import org.atemsource.atem.service.gform.TypeCreator;

public class ReferenceTypeCreator extends TypeCreator {

	private RefMetaDataManager refMetaDataManager;

	@Override
	public void create(TypeBuilder typeBuilder, Attribute<?, ?> attribute) {
		typeBuilder.type("ref");
		RefType refType = (RefType) attribute.getTargetType();

		typeBuilder.getNode().put("schemaUrl",
				refMetaDataManager.getSchemaUri(refType.getTargetType().getCode()));
		typeBuilder.getNode().put("url",
				refMetaDataManager.getSearchUri(refType.getTargetType().getCode()));

		typeBuilder.getNode().put("idProperty",
				refMetaDataManager.getIdAttribute(refType.getTargetType().getCode()));
		typeBuilder.getNode().put("searchProperty",
				refMetaDataManager.getSearchAttribute(refType.getTargetType().getCode()));

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
