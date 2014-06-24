package org.atemsource.atem.service.gform.creator;

import java.text.SimpleDateFormat;

import javax.annotation.Resource;

import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.api.type.EntityTypeBuilder;
import org.atemsource.atem.impl.common.attribute.AbstractAttribute;
import org.atemsource.atem.service.gform.AttributeBuilder;
import org.atemsource.atem.service.gform.GformContext;
import org.atemsource.atem.service.gform.GformContextFactory;
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

@ContextConfiguration(locations = {"classpath:/test/atem/gform/json.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class PrimitiveAttributeCreatorTest {
	
	@Resource(name="atem-json-repository")
	private DynamicEntityTypeSubrepository<ObjectNode> jsonRepository;
	private PrimitiveAttributeCreator creator;
	private EntityTypeBuilder builder;
	private ObjectMapper mapper;
	private ObjectNode node;
	
	
	@Before
	public void setup() {
		builder = jsonRepository.createBuilder("test"+TestUtils.getNextId());
		GformContext ctx = new GformContextFactory().newInstance();
		creator = new PrimitiveAttributeCreator();
		creator.setCtx(ctx);
		creator.addMixin(new RequiredMixin());
		mapper = new ObjectMapper();
		node = mapper.createObjectNode();
	}

	@Test
	public void testString() {
		
		builder.addSingleAttribute("test",String.class);
		
		EntityType<?> entityType = builder.createEntityType();
		Assert.assertNotNull(entityType);
		
		Assert.assertTrue(creator.handles(entityType.getAttribute("test")));
		creator.create(createAttributeBuilder(),entityType.getAttribute("test"));
		Assert.assertEquals("string", node.get("type").getTextValue());
		Assert.assertEquals("test", node.get("code").getTextValue());
	}

	@Test
	public void testPossibleValues() {
		
		SingleAttribute<String> attribute = builder.addSingleAttribute("test",String.class);
		attribute.setMetaValue(PossibleValues.META_ATTRIBUTE_CODE, new StringPossibleValues(new String[]{"jj","kk"}));
		
		creator.create(createAttributeBuilder(),attribute);
		Assert.assertTrue(node.get("values") instanceof ArrayNode);
		Assert.assertEquals(2, ((ArrayNode)node.get("values")).size());
	}

	@Test
	public void testRequired() {
		
		SingleAttribute<String> attribute = builder.addSingleAttribute("test",String.class);
		((AbstractAttribute)attribute).setRequired(true);
		
		creator.create(createAttributeBuilder(),attribute);
		Assert.assertTrue(node.get("required").getBooleanValue());
	}

	private AttributeBuilder createAttributeBuilder() {
		return new AttributeBuilder(mapper, node);
	}
	

	@Test
	public void testDateFormat() {
		
		SingleAttribute<String> attribute = builder.addSingleAttribute("test",String.class);
		attribute.setMetaValue(DateFormat.META_ATTRIBUTE_CODE, new DateFormat("dd.MM.yyyy",new SimpleDateFormat("dd.MM.yyyy")));
		
		creator.create(createAttributeBuilder(),attribute);
		Assert.assertEquals("dd.MM.yyyy",node.get("format").getTextValue());
	}
	
	

}
