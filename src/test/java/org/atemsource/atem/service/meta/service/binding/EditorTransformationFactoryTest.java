package org.atemsource.atem.service.meta.service.binding;

import static org.junit.Assert.*;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.service.meta.service.binding.editor.EditorTransformationFactory;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"classpath:/test/atem/service/meta/editor.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class EditorTransformationFactoryTest {

	@Inject
	private EditorTransformationFactory editorTransformationFactory;
	
	@Inject
	private EntityTypeRepository entityTypeRepository;
	
	@Resource(name="atem-json-repository")
	private DynamicEntityTypeSubrepository<ObjectNode> subRepository;
	
	@Before
	public void createTypes() {
		if (entityTypeRepository.getEntityType("test1")==null) {

			EntityTypeBuilder builder3 = subRepository.createBuilder("test3");
			EntityType<?> superType = builder3.createEntityType();

			EntityTypeBuilder builder = subRepository.createBuilder("test1");
			builder.addSingleAttribute("astring", String.class);
			builder.addSingleAttribute("adouble", Double.class);
			builder.addSingleAttribute("aboolean", Boolean.class);
			builder.superType(superType);
			EntityType<?> test1 = builder.createEntityType();
			
			EntityTypeBuilder builder2 = subRepository.createBuilder("test2");
			builder2.addSingleAttribute("astring", String.class);
			builder2.addSingleAttribute("adouble", Double.class);
			builder2.addSingleAttribute("aboolean", Boolean.class);
			builder2.superType(superType);
			EntityType<?> test2 = builder2.createEntityType();
			
			EntityTypeBuilder builder4 = subRepository.createBuilder("test4");
			builder4.addSingleAttribute("aTest",superType);
			EntityType<?> test4 = builder4.createEntityType();
			

		}
	}
	
	@Test
	public void testSimpleType() {
		EntityType<ObjectNode> test1 = entityTypeRepository.getEntityType("test1");
		SimpleTransformationContext ctx = new SimpleTransformationContext(entityTypeRepository);
		ObjectNode schema = (ObjectNode) editorTransformationFactory.getTransformation().getAB().convert(test1, ctx);
		System.out.println(schema);
	}
	
	@Test
	public void testPolymorphismType() {
		EntityType<ObjectNode> test4 = entityTypeRepository.getEntityType("test4");
		SimpleTransformationContext ctx = new SimpleTransformationContext(entityTypeRepository);
		ObjectNode schema = (ObjectNode) editorTransformationFactory.getTransformation().getAB().convert(test4, ctx);
		System.out.println(schema);
	}
	


}
