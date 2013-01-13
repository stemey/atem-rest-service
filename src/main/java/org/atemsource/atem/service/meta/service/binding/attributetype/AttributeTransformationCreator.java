package org.atemsource.atem.service.meta.service.binding.attributetype;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.Type;
import org.atemsource.atem.utility.transform.api.TransformationBuilderFactory;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;

public interface AttributeTransformationCreator {
public EntityTypeTransformation<?,?> addAttributeTransformation(TypeTransformationBuilder<Attribute, ?> transformationBuilder);
public boolean canTransform(Attribute<?,?> attribute);
public String getTargetName();

}
