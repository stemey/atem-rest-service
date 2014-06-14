package org.atemsource.atem.service.meta.service.binding.validation;

import javax.validation.constraints.NotNull;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.primitive.IntegerType;
import org.atemsource.atem.service.meta.service.binding.AttributeMixin;
import org.atemsource.atem.service.meta.service.binding.ClassUtils;
import org.atemsource.atem.service.meta.service.binding.attributetype.AttributeTransformationCreator;
import org.atemsource.atem.utility.transform.api.JavaTransformation;
import org.atemsource.atem.utility.transform.api.JavaUniConverter;
import org.atemsource.atem.utility.transform.api.TransformationContext;
import org.atemsource.atem.utility.transform.api.TypeTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.builder.Constant;
import org.atemsource.atem.utility.transform.impl.builder.GenericTransformationBuilder;
import org.atemsource.atem.utility.transform.impl.builder.Mixin;
import org.codehaus.jackson.node.ObjectNode;

public class RequiredMixin implements AttributeMixin {

	@Override
	public void mixin(TypeTransformationBuilder<?, ?> builder) {
		builder.transformCustom(GenericTransformationBuilder.class).transform(new JavaTransformation<Attribute,ObjectNode>() {

			@Override
			public void mergeAB(Attribute a, ObjectNode b, TransformationContext ctx) {
				Attribute metaAttribute = a.getMetaAttribute(ClassUtils.getMetaAttributeName(javax.validation.constraints.NotNull.class));
				b.put("required", (metaAttribute!=null && metaAttribute.getValue(a)!=null) || a.isRequired());
			}

			@Override
			public void mergeBA(ObjectNode b, Attribute a, TransformationContext ctx) {
				
			}
		}).to().addSingleAttribute("required", boolean.class);
		
		
		
		
	}

	

}
