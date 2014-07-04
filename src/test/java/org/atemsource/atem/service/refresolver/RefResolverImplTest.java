package org.atemsource.atem.service.refresolver;

import javax.inject.Inject;

import junit.framework.Assert;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.entity.TypeAndId;
import org.atemsource.atem.service.refresolver.example.Product;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@ContextConfiguration(locations = { "classpath:/test/atem/refresolver/refresolver.xml" })
@RunWith(SpringJUnit4ClassRunner.class)
public class RefResolverImplTest {

	@Inject
	private RefResolver refResolver;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	@Test
	public void testParseSingleUri() {
		TypeAndId typeAndId = refResolver.parseSingleUri("/xx/json:org.atemsource.atem.service.refresolver.example.Product/3");
		Assert.assertEquals(Product.class, typeAndId.getOriginalType()
				.getJavaType());
		Assert.assertEquals(3, typeAndId.getOriginalId());
		Assert.assertEquals("json:org.atemsource.atem.service.refresolver.example.Product", typeAndId.getEntityType().getCode());
		Assert.assertEquals(3, typeAndId.getOriginalId());
	}
	
	@Test
	public void testGetSingleUri() {
		String uri=refResolver.getSingleUri(entityTypeRepository.getEntityType(Product.class),3);
		Assert.assertEquals("/xx/org.atemsource.atem.service.refresolver.example.Product/3", uri);
	}

	@Test
	public void testGetCollectionUri() {
		String uri=refResolver.getCollectionUri(entityTypeRepository.getEntityType(Product.class));
		Assert.assertEquals("/xx/org.atemsource.atem.service.refresolver.example.Product", uri);
	}

	@Test
	public <O,T>void testParseUri() {
		CollectionResource<O, T> resource=refResolver.parseUri("/xx/json:org.atemsource.atem.service.refresolver.example.Product");
		Assert.assertFalse(resource instanceof TypeAndId);
	}

	@Test
	public void testParseCollectionUri() {
		CollectionResource collectionResource = refResolver.parseCollectionUri("/xx/json:org.atemsource.atem.service.refresolver.example.Product");
		Assert.assertEquals(Product.class, collectionResource.getOriginalType().getJavaType());
		Assert.assertEquals("json:org.atemsource.atem.service.refresolver.example.Product", collectionResource.getEntityType().getCode());
	}

	

}
