package org.atemsource.atem.service.meta.service.binding;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.impl.common.attribute.PrimitiveAttributeImpl;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"classpath:/test/atem/service/meta/editor.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class AttributeTransformationFactoryTest {

	@Inject
	private AttributeTransformationFactory attributeTransformationFactory;
	
	@Inject
	private EntityTypeRepository entityTypeRepository;
	
	@Test
	public void test() {
		EntityTypeTransformation<Object, ObjectNode> reference = (EntityTypeTransformation<Object, ObjectNode>) attributeTransformationFactory.getReference();
		PrimitiveAttributeImpl<String> attribute = new PrimitiveAttributeImpl<String>() ;
		attribute.setCode("astr");
		attribute.setTargetType(entityTypeRepository.getType(String.class));
		ObjectNode objectNode = reference.getAB().convert(attribute, new SimpleTransformationContext(entityTypeRepository));
		System.out.println(objectNode);
	}

}
