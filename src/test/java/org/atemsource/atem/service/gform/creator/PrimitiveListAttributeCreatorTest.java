package org.atemsource.atem.service.gform.creator;

import static org.junit.Assert.*;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.atemsource.atem.api.attribute.CollectionSortType;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.impl.common.attribute.primitive.PrimitiveTypeFactory;
import org.atemsource.atem.service.gform.AttributeBuilder;
import org.atemsource.atem.service.gform.GformContext;
import org.atemsource.atem.service.gform.GformContextFactory;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:/test/atem/gform/json.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class PrimitiveListAttributeCreatorTest {

	@Resource(name = "atem-json-repository")
	private DynamicEntityTypeSubrepository<ObjectNode> jsonRepository;
	@Inject
	private PrimitiveTypeFactory primitiveTypeFactory;
	private PrimitiveListAttributeCreator creator;
	private EntityTypeBuilder builder;
	private ObjectMapper mapper;
	private ObjectNode node;

	
	@Before
	public void setup() {
		builder = jsonRepository.createBuilder("test_primitive_list" + TestUtils.getNextId());
		GformContext ctx = new GformContextFactory().newInstance();
		creator = new PrimitiveListAttributeCreator();
		creator.setCtx(ctx);
		mapper = new ObjectMapper();
		node = mapper.createObjectNode();
	}
	private AttributeBuilder createAttributeBuilder() {
		return new AttributeBuilder(mapper, node);
	}
	
	@Test
	public void test() {
		builder.addMultiAssociationAttribute("test", primitiveTypeFactory.getPrimitiveType(String.class),CollectionSortType.ORDERABLE);

		EntityType<?> entityType = builder.createEntityType();
		Assert.assertNotNull(entityType);

		Assert.assertTrue(creator.handles(entityType.getAttribute("test")));
		creator.create(createAttributeBuilder(),
				entityType.getAttribute("test"));
		Assert.assertEquals("array", node.get("type").getTextValue());
		Assert.assertEquals("test", node.get("code").getTextValue());
		ObjectNode element = (ObjectNode) node.get("element");
		Assert.assertEquals("string", element.get("type").getTextValue());
	}

}
