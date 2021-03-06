package org.atemsource.atem.service.entity.collection;

import junit.framework.Assert;

import org.atemsource.atem.api.infrastructure.util.ReflectionUtils;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.common.attribute.SingleAttributeImpl;
import org.atemsource.atem.impl.common.attribute.primitive.SimpleTextType;
import org.atemsource.atem.service.entity.search.Operator;
import org.atemsource.atem.service.entity.search.Query;
import org.atemsource.atem.service.refresolver.CollectionResource;
import org.atemsource.atem.utility.path.AttributePath;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.transform.impl.EntityTypeTransformation;
import org.atemsource.atem.utility.transform.impl.transformation.AbstractOneToOneAttributeTransformation;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;

public class GridxQueryParserTest {
	private GridxQueryParser gridxQueryParser;
	
	

	@Test
	public <O, T> void testSimpleComparison() {
		
		gridxQueryParser = new GridxQueryParser();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				org.codehaus.jackson.JsonParser.Feature.ALLOW_SINGLE_QUOTES,
				true);
		ReflectionUtils.setField(gridxQueryParser, "objectMapper", mapper);
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter(
				"query",
				"{'op':'or','data':[{'op':'equal','data':[{'data':'name'},{'data':'William'}]}]}]}");
		EntityType entityType = Mockito.mock(EntityType.class);
		EntityType originalType = Mockito.mock(EntityType.class);
		DerivedType derivedType = Mockito.mock(DerivedType.class);
		EntityTypeTransformation entityTypeTransformation = Mockito.mock(EntityTypeTransformation.class);
		AbstractOneToOneAttributeTransformation attributeTransformation = Mockito.mock(AbstractOneToOneAttributeTransformation.class);
		AttributePath attributePath = Mockito.mock(AttributePath.class);
		Mockito.when(derivedType.getOriginalType()).thenReturn(originalType);
		Mockito.when(derivedType.getTransformation()).thenReturn(entityTypeTransformation);
		Mockito.when(entityTypeTransformation.getAttributeTransformationByB("name")).thenReturn(attributeTransformation);
		Mockito.when(attributeTransformation.convertBA("William", null)).thenReturn("William");
		Mockito.when(attributeTransformation.getAttributeB()).thenReturn(attributePath);
		Mockito.when(attributeTransformation.getAttributeA()).thenReturn(attributePath);
		
		CollectionResource resource = new CollectionResource(derivedType,
				entityType);

		SingleAttributeImpl nameAttribute = new SingleAttributeImpl();
		nameAttribute.setTargetType(new SimpleTextType());
		Mockito.when(originalType.getAttribute("name")).thenReturn(
				nameAttribute);
		
		Mockito.when(attributePath.getAttribute()).thenReturn(nameAttribute);

		Query query = gridxQueryParser.parseQuery(req, resource);
		Assert.assertEquals(1, query.getPredicates().size());
		Assert.assertEquals(nameAttribute, query.getPredicates().get(0)
				.getAttribute());
		Assert.assertEquals(Operator.EQUAL, query.getPredicates().get(0)
				.getOperator());
		Assert.assertEquals("William", query.getPredicates().get(0).getValue());
		Assert.assertTrue(query.isOr());

	}

	@Test
	public <O, T> void testAndComparison() {
		gridxQueryParser = new GridxQueryParser();
		ObjectMapper mapper = new ObjectMapper();
		mapper.configure(
				org.codehaus.jackson.JsonParser.Feature.ALLOW_SINGLE_QUOTES,
				true);
		ReflectionUtils.setField(gridxQueryParser, "objectMapper", mapper);
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter(
				"query",
				"{'op':'and','data':[{'op':'equal','data':[{'data':'name'},{'data':'William'}]}]}]}");
		EntityType entityType = Mockito.mock(EntityType.class);
		EntityType originalType = Mockito.mock(EntityType.class);
		DerivedType derivedType = Mockito.mock(DerivedType.class);
		EntityTypeTransformation entityTypeTransformation = Mockito.mock(EntityTypeTransformation.class);
		AbstractOneToOneAttributeTransformation attributeTransformation = Mockito.mock(AbstractOneToOneAttributeTransformation.class);
		AttributePath attributePath = Mockito.mock(AttributePath.class);
		Mockito.when(derivedType.getOriginalType()).thenReturn(originalType);
		Mockito.when(derivedType.getTransformation()).thenReturn(entityTypeTransformation);
		Mockito.when(entityTypeTransformation.getAttributeTransformationByB("name")).thenReturn(attributeTransformation);
		Mockito.when(attributeTransformation.convertBA("William", null)).thenReturn("William");
		Mockito.when(attributeTransformation.getAttributeB()).thenReturn(attributePath);
		Mockito.when(attributeTransformation.getAttributeA()).thenReturn(attributePath);

		
		derivedType.setOriginalType(originalType);
		CollectionResource resource = new CollectionResource(derivedType,
				entityType);

		SingleAttributeImpl nameAttribute = new SingleAttributeImpl();
		nameAttribute.setTargetType(new SimpleTextType());
		Mockito.when(originalType.getAttribute("name")).thenReturn(
				nameAttribute);
		
		Mockito.when(attributePath.getAttribute()).thenReturn(nameAttribute);

		Query query = gridxQueryParser.parseQuery(req, resource);
		Assert.assertEquals(1, query.getPredicates().size());
		Assert.assertFalse(query.isOr());
		Assert.assertEquals(nameAttribute, query.getPredicates().get(0)
				.getAttribute());
		Assert.assertEquals(Operator.EQUAL, query.getPredicates().get(0)
				.getOperator());
		Assert.assertEquals("William", query.getPredicates().get(0).getValue());

	}
}
