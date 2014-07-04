package org.atemsource.atem.service.entity.collection;

import junit.framework.Assert;

import org.atemsource.atem.api.infrastructure.util.ReflectionUtils;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.impl.common.attribute.SingleAttributeImpl;
import org.atemsource.atem.service.entity.search.Operator;
import org.atemsource.atem.service.entity.search.Query;
import org.atemsource.atem.service.entity.search.Sorting;
import org.atemsource.atem.service.refresolver.CollectionResource;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.codehaus.jackson.map.ObjectMapper;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;


public class GridxSortingParserTest {
	private GridxSortingParser gridxSortingParser;

	@Test
	public <O, T> void testSimpleComparison() {
		gridxSortingParser = new GridxSortingParser<O, T>();
		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addParameter(
				"sortBy",
				"-name, count");
		EntityType entityType = Mockito.mock(EntityType.class);
		EntityType originalType = Mockito.mock(EntityType.class);
		DerivedType<O, T> derivedType = new DerivedType<O, T>();
		derivedType.setOriginalType(originalType);
		CollectionResource resource = new CollectionResource(derivedType,
				entityType);

		SingleAttributeImpl nameAttribute = new SingleAttributeImpl();
		Mockito.when(originalType.getAttribute("name")).thenReturn(
				nameAttribute);
		SingleAttributeImpl countAttribute = new SingleAttributeImpl();
		Mockito.when(originalType.getAttribute("count")).thenReturn(
				countAttribute);

		Sorting sorting = gridxSortingParser.parseSorting(req, resource);
		Assert.assertEquals(2, sorting.getAttributeSortings().size());
		Assert.assertEquals(nameAttribute,sorting.getAttributeSortings().get(0).getAttribute());
		Assert.assertEquals(false,sorting.getAttributeSortings().get(0).isAsc());
	}
}
