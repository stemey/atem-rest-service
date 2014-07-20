package org.atemsource.atem.service.gform.creator;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.atemsource.atem.api.attribute.Attribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.service.gform.AttributeBuilder;
import org.atemsource.atem.service.gform.GformContext;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:/test/atem/gform/json.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class EmbeddedAttributeCreatorTest {

	@Resource(name = "atem-json-repository")
	private DynamicEntityTypeSubrepository<ObjectNode> jsonRepository;
	private EmbeddedAttributeCreator creator;
	private EntityTypeBuilder builder;
	private ObjectMapper mapper;
	private ObjectNode node;
	private EntityType<?> embeddedType;
	@Inject
	private GformContext ctx;


	@Before
	public void setup() {
		builder = jsonRepository.createBuilder("test_embedded" + TestUtils.getNextId());
		EntityTypeBuilder embedded = jsonRepository.createBuilder("embedded"
				+ TestUtils.getNextId());
		embedded.addSingleAttribute("e1", String.class);
		embedded.addSingleAttribute("e2", Boolean.class);
		this.embeddedType = embedded.createEntityType();
		
		creator=new EmbeddedAttributeCreator();
		creator.setCtx(ctx);

		mapper = new ObjectMapper();
		node = mapper.createObjectNode();
	}

	@Test
	public void testEmbedded() {

		Attribute<?,?> attribute = builder.addSingleAssociationAttribute("test",
				embeddedType);

		EntityType<?> entityType = builder.createEntityType();
		
		creator.create(new AttributeBuilder(mapper, node), attribute);

		Assert.assertNotNull(entityType);

		Assert.assertTrue(creator.handles(entityType.getAttribute("test")));
		Assert.assertEquals("object", node.get("type").getTextValue());
		Assert.assertEquals("test", node.get("code").getTextValue());
		
		ObjectNode group = (ObjectNode) node.get("group");
		ArrayNode attributes=(ArrayNode) group.get("attributes");
		Assert.assertEquals(2, attributes.size());
	}

}
