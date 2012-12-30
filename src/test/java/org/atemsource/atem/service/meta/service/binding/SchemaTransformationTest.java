package org.atemsource.atem.service.meta.service.binding;

import static org.junit.Assert.assertNotNull;

import javax.inject.Inject;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.utility.transform.api.SimpleTransformationContext;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

	@ContextConfiguration(locations = {"classpath:/test/atem/service/meta/editor.xml"})
	@RunWith(SpringJUnit4ClassRunner.class)
	public class SchemaTransformationTest {

		@Inject
		private EditorTransformationFactory editorTransformationFactory;
		
		@Inject
		private SchemaTransformationFactory schemaTransformationFactory;
		
		@Inject
		private EntityTypeRepository entityTypeRepository;
		
		
		@Test
		public void testSchemaTypeAttributes() {
			EntityType<?> entityType = (EntityType<?>) editorTransformationFactory.getTransformation().getTypeB();
			assertNotNull(entityType.getAttribute("type-property"));
			assertNotNull(entityType.getAttribute("attributes"));
			assertNotNull(entityType.getAttribute("code"));
			assertNotNull(entityType.getAttribute("label"));
		}
		@Test
		public void testAttributeTypeAttributes() {
			EntityType<?> targetType = getAttributeType();
			assertNotNull(targetType.getAttribute("code"));
			assertNotNull(targetType.getAttribute("label"));
			assertNotNull(targetType.getAttribute("type"));
			assertNotNull(targetType.getAttribute("dateformat"));
			assertNotNull(targetType.getAttribute("validTypes"));
			assertNotNull(targetType.getAttribute("values"));
		}
		
		@Test
		public void testEditorSchema() {
			EntityType<?> entityType = (EntityType<?>) editorTransformationFactory.getTransformation().getTypeB();
			 ObjectNode schemaSchema = (ObjectNode) schemaTransformationFactory.getTransformation().getAB().convert(entityType, new SimpleTransformationContext(entityTypeRepository));
			 System.out.println(entityType.getCode()+":");
			 System.out.println(schemaSchema);
		}
		@Test
		public void testEditorAttributeSchema() {
			EntityType<?> entityType = entityTypeRepository.getEntityType("editor:attribute");
			 ObjectNode schemaSchema = (ObjectNode) schemaTransformationFactory.getTransformation().getAB().convert(entityType, new SimpleTransformationContext(entityTypeRepository));
			 System.out.println(entityType.getCode()+":");
			 System.out.println(schemaSchema);
		}
		
		@Test
		public void testEditorListAttributeSchema() {
			EntityType<?> entityType = entityTypeRepository.getEntityType("editor:array-attribute");
			 ObjectNode schemaSchema = (ObjectNode) schemaTransformationFactory.getTransformation().getAB().convert(entityType, new SimpleTransformationContext(entityTypeRepository));
			 System.out.println(entityType.getCode()+":");
			 System.out.println(schemaSchema);
		}
		
		@Test
		public void testEditorSingleAttributeSchema() {
			EntityType<?> entityType = entityTypeRepository.getEntityType("editor:single-attribute");
			 ObjectNode schemaSchema = (ObjectNode) schemaTransformationFactory.getTransformation().getAB().convert(entityType, new SimpleTransformationContext(entityTypeRepository));
			 System.out.println(entityType.getCode()+":");
			 System.out.println(schemaSchema);
		}
		
		
		
		@Test
		public void testSchemaSchema() {
			EntityType<?> entityType = (EntityType<?>) schemaTransformationFactory.getTransformation().getTypeB();
			 ObjectNode schemaSchema = (ObjectNode) schemaTransformationFactory.getTransformation().getAB().convert(entityType, new SimpleTransformationContext(entityTypeRepository));
			 System.out.println(entityType.getCode()+":");
			 System.out.println(schemaSchema);
		}
		
		@Test
		public void testAttributeSchema() {
			EntityType<?> entityType = entityTypeRepository.getEntityType("schema:attribute");
			 ObjectNode schemaSchema = (ObjectNode) schemaTransformationFactory.getTransformation().getAB().convert(entityType, new SimpleTransformationContext(entityTypeRepository));
			 System.out.println(entityType.getCode()+":");
			 System.out.println(schemaSchema);
		}
		
		@Test
		public void testListAttributeSchema() {
			EntityType<?> entityType = entityTypeRepository.getEntityType("schema:array-attribute");
			 ObjectNode schemaSchema = (ObjectNode) schemaTransformationFactory.getTransformation().getAB().convert(entityType, new SimpleTransformationContext(entityTypeRepository));
			 System.out.println(entityType.getCode()+":");
			 System.out.println(schemaSchema);
		}
		
		@Test
		public void testSingleAttributeSchema() {
			EntityType<?> entityType = entityTypeRepository.getEntityType("schema:single-attribute");
			 ObjectNode schemaSchema = (ObjectNode) schemaTransformationFactory.getTransformation().getAB().convert(entityType, new SimpleTransformationContext(entityTypeRepository));
			 System.out.println(entityType.getCode()+":");
			 System.out.println(schemaSchema);
		}
		
		@Test
		public void testTypeRefSchema() {
			EntityType<?> entityType = entityTypeRepository.getEntityType("schema:type-ref");
			 ObjectNode schemaSchema = (ObjectNode) schemaTransformationFactory.getTransformation().getAB().convert(entityType, new SimpleTransformationContext(entityTypeRepository));
			 System.out.println(entityType.getCode()+":");
			 System.out.println(schemaSchema);
		}
		
		@Test
		public void testPrimitiveTypeRefSchema() {
			EntityType<?> entityType = entityTypeRepository.getEntityType("schema:primitivetype-ref");
			 ObjectNode schemaSchema = (ObjectNode) schemaTransformationFactory.getTransformation().getAB().convert(entityType, new SimpleTransformationContext(entityTypeRepository));
			 System.out.println(entityType.getCode()+":");
			 System.out.println(schemaSchema);
		}
		
		@Test
		public void testEntityTypeRefSchema() {
			EntityType<?> entityType = entityTypeRepository.getEntityType("schema:entitytype-ref");
			 ObjectNode schemaSchema = (ObjectNode) schemaTransformationFactory.getTransformation().getAB().convert(entityType, new SimpleTransformationContext(entityTypeRepository));
			 System.out.println(entityType.getCode()+":");
			 System.out.println(schemaSchema);
		}
		
		protected EntityType<?> getAttributeType() {
			EntityType<?> entityType = (EntityType<?>) editorTransformationFactory.getTransformation().getTypeB();
			Attribute<?,?> attributes = entityType.getAttribute("attributes");
			assertNotNull(attributes);
			EntityType<?> targetType = (EntityType<?>) attributes.getTargetType();
			return targetType;
		}

}
