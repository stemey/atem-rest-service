package org.atemsource.atem.service.gform.creator;

import java.text.SimpleDateFormat;

import javax.annotation.Resource;
import javax.inject.Inject;

import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.impl.common.attribute.AbstractAttribute;
import org.atemsource.atem.service.gform.AttributeBuilder;
import org.atemsource.atem.service.gform.GformContext;
import org.atemsource.atem.spi.DynamicEntityTypeSubrepository;
import org.atemsource.atem.utility.transform.api.constraint.DateFormat;
import org.atemsource.atem.utility.transform.api.constraint.PossibleValues;
import org.atemsource.atem.utility.transform.impl.converter.StringPossibleValues;
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
public class NumberAttributeCreatorTest {

	@Resource(name = "atem-json-repository")
	private DynamicEntityTypeSubrepository<ObjectNode> jsonRepository;
	private PrimitiveAttributeCreator creator;
	private EntityTypeBuilder builder;
	private ObjectMapper mapper;
	private ObjectNode node;
	@Inject
	private GformContext ctx;

	@Before
	public void setup() {
		builder = jsonRepository.createBuilder("test" + TestUtils.getNextId());
		creator = new PrimitiveAttributeCreator();
		creator.setCtx(ctx);
		creator.addMixin(new RequiredMixin());
		mapper = new ObjectMapper();
		node = mapper.createObjectNode();
	}

	@Test
	public void testInteger() {

		builder.addSingleAttribute("test", Integer.class);

		EntityType<?> entityType = builder.createEntityType();
		Assert.assertNotNull(entityType);

		Assert.assertTrue(creator.handles(entityType.getAttribute("test")));
		creator.create(createAttributeBuilder(),
				entityType.getAttribute("test"));
		Assert.assertEquals("number", node.get("type").getTextValue());
		Assert.assertEquals("test", node.get("code").getTextValue());
	}


	@Test
	public void testDouble() {

		builder.addSingleAttribute("test", double.class);

		EntityType<?> entityType = builder.createEntityType();
		Assert.assertNotNull(entityType);

		Assert.assertTrue(creator.handles(entityType.getAttribute("test")));
		creator.create(createAttributeBuilder(),
				entityType.getAttribute("test"));
		Assert.assertEquals("number", node.get("type").getTextValue());
		Assert.assertEquals("test", node.get("code").getTextValue());
	}



	private AttributeBuilder createAttributeBuilder() {
		return new AttributeBuilder(mapper, node);
	}


}
