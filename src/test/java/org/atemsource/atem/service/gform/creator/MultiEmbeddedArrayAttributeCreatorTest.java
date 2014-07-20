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
public class MultiEmbeddedArrayAttributeCreatorTest {

	@Resource(name = "atem-json-repository")
	private DynamicEntityTypeSubrepository<ObjectNode> jsonRepository;
	private MultiEmbeddedAttributeCreator creator;
	private EntityTypeBuilder builder;
	private ObjectMapper mapper;
	private ObjectNode node;
	private EntityType<?> embeddedType;
	@Inject
	private GformContext ctx;

	private EntityType<?> subType1;
	private EntityType<?> subType2;

	@Before
	public void setup() {
		builder = jsonRepository.createBuilder("test" + TestUtils.getNextId());
		EntityTypeBuilder superEmbedded = jsonRepository.createBuilder("super"
				+ TestUtils.getNextId());
		superEmbedded.addSingleAttribute("e1", String.class);
		superEmbedded.addSingleAttribute("e2", Boolean.class);
		this.embeddedType = superEmbedded.createEntityType();
		
		EntityTypeBuilder subBuilder1 = jsonRepository.createBuilder("sub1" + TestUtils.getNextId());
		subBuilder1.superType(embeddedType);
		subBuilder1.addSingleAttribute("subProp1", String.class);
		subType1 = subBuilder1.createEntityType();
		
		EntityTypeBuilder subBuilder2 = jsonRepository.createBuilder("sub2" + TestUtils.getNextId());
		subBuilder2.superType(embeddedType);
		subBuilder2.addSingleAttribute("subProp2", Boolean.class);
		subType2 = subBuilder2.createEntityType();

		creator = new MultiEmbeddedAttributeCreator();
		creator.setCtx(ctx);
		mapper = new ObjectMapper();
		node = mapper.createObjectNode();
	}

	@Test
	public void testSub1() {

		Attribute<?,?> attribute = builder.addSingleAssociationAttribute("test",
				embeddedType);

		EntityType<?> entityType = builder.createEntityType();
		
		creator.create(new AttributeBuilder(mapper, node), attribute);

		Assert.assertNotNull(entityType);

		Assert.assertTrue(creator.handles(entityType.getAttribute("test")));
		Assert.assertEquals("object", node.get("type").getTextValue());
		Assert.assertEquals("test", node.get("code").getTextValue());
		
		ArrayNode groups = (ArrayNode) node.get("groups");
		ArrayNode attributes=(ArrayNode) groups.get(0).get("attributes");
		Assert.assertEquals(subType1.getCode(), groups.get(0).get("code").getTextValue());
		Assert.assertEquals(3, attributes.size());
		Assert.assertEquals("ext_type", node.get("typeProperty").getTextValue());
	}

}
