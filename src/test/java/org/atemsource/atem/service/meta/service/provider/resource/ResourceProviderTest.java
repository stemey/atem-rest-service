package org.atemsource.atem.service.meta.service.provider.resource;

import java.util.Iterator;
import java.util.List;

import javax.inject.Inject;

import junit.framework.Assert;

import org.atemsource.atem.service.meta.service.model.resource.Resource;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = {"classpath:/test/atem/service/provider/resource.xml"})
@RunWith(SpringJUnit4ClassRunner.class)
public class ResourceProviderTest {
	
	@Inject
	private ResourceProvider resourceProvider;

	@Test
	public void testIntegration() {
		List<Resource> services = resourceProvider.getServices();
		Assert.assertEquals(2, services.size());
		Iterator<Resource> iterator = services.iterator();
		Resource resource1 = iterator.next();
		
		Resource resource2 = iterator.next();
	}

}
