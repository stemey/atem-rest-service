package org.atemsource.atem.service;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;

import org.atemsource.atem.service.jpa.example.Feature;
import org.atemsource.atem.service.meta.service.http.MetaRestService;
import org.bouncycastle.asn1.pkcs.Attribute;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:/atem/service/application.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class JpaMetaIntegrationTest {

	@Inject
	private MetaRestService metaRestService;

	private ObjectMapper objectMapper = new ObjectMapper();

	@Test
	public void testGetResourceListing() throws IOException, ServletException {
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		String uri = "/meta";

		req.setRequestURI(uri);
		metaRestService.doGet(req, resp);
		if (resp.getStatus() != HttpServletResponse.SC_OK) {
			Assert.fail(resp.getContentAsString());
		}

		ObjectNode result = (ObjectNode) objectMapper.readTree(resp
				.getContentAsString());

		ArrayNode groups = (ArrayNode) result.get("groups");
		Assert.assertEquals(1, groups.size());
		ArrayNode services = (ArrayNode) groups.get(0).get("services");
		Assert.assertEquals(4, services.size());
		System.out.println(result.toString());

		for (int i = 0; i < services.size(); i++) {
			JsonNode service = services.get(i);
			checkResourceExists(service, service.get("schemaUrl")
					.getTextValue());
			checkResourceExists(service, service.get("collectionSchemaUrl")
					.getTextValue());
			checkResourceExists(service, service.get("resourceUrl")
					.getTextValue());
			Assert.assertTrue(service.get("idProperty") != null);
		}

	}
	
	@Test
	public void testGetSchema() throws IOException, ServletException {
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		String uri = "/meta/json:"+Feature.class.getName();

		req.setRequestURI(uri);
		metaRestService.doGet(req, resp);
		if (resp.getStatus() != HttpServletResponse.SC_OK) {
			Assert.fail(resp.getContentAsString());
		}

		ObjectNode result = (ObjectNode) objectMapper.readTree(resp
				.getContentAsString());

		ArrayNode attributes = (ArrayNode) result.get("attributes");
		Assert.assertEquals(2, attributes.size());
		
		for (int i = 0; i < attributes.size(); i++) {
			JsonNode attribute = attributes.get(i);
			Assert.assertNotNull(attribute.get("type").getTextValue());
			Assert.assertNotNull(attribute.get("code").getTextValue());
		}

	}

	private void checkResourceExists(JsonNode service, String uri)
			throws ServletException, IOException {
		MockHttpServletRequest req = new MockHttpServletRequest();
		MockHttpServletResponse resp = new MockHttpServletResponse();
		req.setRequestURI(uri);
		metaRestService.doGet(req, resp);
		Assert.assertEquals(HttpServletResponse.SC_OK, resp.getStatus());
	}

}
