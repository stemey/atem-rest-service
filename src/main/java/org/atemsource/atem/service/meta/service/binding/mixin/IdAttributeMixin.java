package org.atemsource.atem.service.meta.service.binding.mixin;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.service.IdentityAttributeService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.api.type.primitive.IntegerType;
import org.atemsource.atem.doc.javadoc.JavadocDataStore;
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
import org.springframework.web.bind.annotation.RequestParam;

public class IdAttributeMixin implements AttributeMixin {

	@Override
	public void mixin(TypeTransformationBuilder<?, ?> builder) {
		builder.transformCustom(GenericTransformationBuilder.class).transform(new JavaTransformation<Object, Object>() {

			@Override
			public void mergeAB(Object a, Object b, TransformationContext ctx) {
				IdentityAttributeService identityAttributeService = ctx.getEntityTypeByA(a).getService(
						IdentityAttributeService.class);
				if (identityAttributeService != null) {
					SingleAttribute<? extends Serializable> idAttribute = identityAttributeService
							.getIdAttribute(((Attribute) a).getEntityType());
					if (a == idAttribute) {
						ctx.getEntityTypeByB(b).getAttribute("disabled").setValue(b, true);
						ctx.getEntityTypeByB(b).getAttribute("required").setValue(b, false);
					}
				}
			}

			@Override
			public void mergeBA(Object b, Object a, TransformationContext ctx) {

			}
		});
	}

}
