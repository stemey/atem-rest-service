package org.atemsource.atem.service.entity.collection;

import javax.servlet.http.HttpServletResponse;

import junit.framework.Assert;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.entity.EntityRestService.Result;
import org.atemsource.atem.service.entity.search.Paging;
import org.atemsource.atem.service.refresolver.CollectionResource;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.junit.Test;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

public class ContentHeaderPagingParserTest {
	@Test
	public <O, T> void test() {
		ContentHeaderPagingParser<O, T> parser = new ContentHeaderPagingParser<O, T>();

		MockHttpServletRequest req = new MockHttpServletRequest();
		req.addHeader("Range", "items=12-10");
		EntityType entityType = Mockito.mock(EntityType.class);
		EntityType originalType = Mockito.mock(EntityType.class);
		DerivedType<O, T> derivedType = new DerivedType<O, T>();
		derivedType.setOriginalType(originalType);
		CollectionResource<O, T> resource = new CollectionResource(derivedType,
				entityType);

		Paging paging = parser.parsePaging(req, resource);
		Assert.assertEquals(12, paging.getStart());
		Assert.assertEquals(10, paging.getCount());
		
		Result result= new Result();
		result.totalCount=102;
		ObjectMapper objectMapper = new ObjectMapper();
		ArrayNode arrayNode = objectMapper.createArrayNode();
		arrayNode.addObject();
		arrayNode.addObject();
		result.entities=arrayNode;
		
		MockHttpServletResponse response= new MockHttpServletResponse();
		parser.addContentRange(response, paging, result);
		Assert.assertEquals("items 12-2/102", response.getHeader("Content-Range"));
	}
}
