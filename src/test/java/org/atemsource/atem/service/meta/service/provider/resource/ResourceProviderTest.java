package org.atemsource.atem.service.meta.service.provider.resource;

import java.util.List;

import javax.inject.Inject;

import junit.framework.Assert;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.jpa.example.Car;
import org.atemsource.atem.service.meta.service.model.resource.Resource;
import org.codehaus.jackson.node.ObjectNode;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:/atem/service/application.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class ResourceProviderTest {

	@javax.annotation.Resource(name = "jpa-provider")
	private ResourceProvider resourceProvider;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Test
	public void testIntegration() {
		List<Resource> services = resourceProvider.getServices();
		Assert.assertEquals(4, services.size());
		EntityType<Car> entityType = entityTypeRepository
				.getEntityType(Car.class);
		Assert.assertTrue(resourceProvider.handles(entityType));
		ObjectNode schema = resourceProvider.getSchema(entityType);
		Assert.assertNotNull(schema);
	}

}
