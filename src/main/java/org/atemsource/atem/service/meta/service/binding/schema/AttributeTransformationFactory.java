package org.atemsource.atem.service.meta.service.binding.schema;


import javax.annotation.PostConstruct;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.attribute.JavaMetaData;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.primitive.NumberType;
import org.atemsource.atem.service.meta.service.annotation.ValidTypes;
import org.atemsource.atem.service.meta.service.binding.AbstractAttributeTransformationFactory;
import org.atemsource.atem.utility.transform.api.JavaTransformation;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.GenericTransformationBuilder;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;


public class AttributeTransformationFactory extends AbstractAttributeTransformationFactory {

	protected void transformComplexAttribute(EntityTypeTransformation complexTypeTransformation,
			TypeTransformationBuilder<Attribute, ?> transformationBuilder) {
		transformationBuilder.transform().from("targetType").to("type").convert(complexTypeTransformation);
	}


}
