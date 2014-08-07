package org.atemsource.atem.service.entity;

import java.io.IOException;
import java.io.Serializable;

import javax.servlet.ServletException;

import org.atemsource.atem.api.infrastructure.util.ReflectionUtils;
import org.atemsource.atem.api.service.DeletionService;
import org.atemsource.atem.api.service.InsertionCallback;
import org.atemsource.atem.api.service.InsertionService;
import org.atemsource.atem.api.service.PersistenceService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.refresolver.CollectionResource;
import org.atemsource.atem.service.refresolver.RefResolver;
import org.atemsource.atem.utility.transform.api.meta.DerivedType;
import org.atemsource.atem.utility.validation.ValidationService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ObjectNode;
import org.jmock.Expectations;
import org.jmock.Mockery;
import org.junit.Assert;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;


public class EntityRestServiceTest
{

	   Mockery context = new Mockery();
		
	
	private EntityRestService entityRestService;

	@Test
	public <O,T>void testDelete() throws IOException
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		entityRestService= new EntityRestService();
		
		
		
		final RefResolver refResolver = context.mock(RefResolver.class);
		ReflectionUtils.setField(entityRestService,"refResolver",refResolver);
		final String uri = "/entity/entities/myType/2";
		request.setRequestURI(uri);
		final EntityType<O> entityType= context.mock(EntityType.class);
		DerivedType<O,T> derivedType=new DerivedType<O,T>();
		derivedType.setOriginalType(entityType);
		final TypeAndId<O,T> typeAndId=new TypeAndId<O,T>(derivedType, 2, null, 2);
		final DeletionService deletionService = context.mock(DeletionService.class);
		
		
		context.checking(new Expectations(){{
			one(refResolver).parseSingleUri(uri);
			will(returnValue(typeAndId));
			
			one(entityType).getService(DeletionService.class);
			will(returnValue(deletionService));
			
			one(deletionService).delete(entityType, 2);
			
		}});
		entityRestService.doDelete(request, response);
		
		Assert.assertEquals(200, response.getStatus());

	}
	
	@Test
	public <O,T> void testUpdate() throws ServletException, IOException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		entityRestService= new EntityRestService();
		final RefResolver refResolver = Mockito.mock(RefResolver.class);
		ReflectionUtils.setField(entityRestService,"refResolver",refResolver);
		final ObjectMapper objectMapper = new ObjectMapper();
		ReflectionUtils.setField(entityRestService,"objectMapper",objectMapper);
		
		request.setContent("{}".getBytes());
		
		
		EntityType originalType = Mockito.mock(EntityType.class);
		EntityType jsonType = Mockito.mock(EntityType.class);
		final String uri = "/entity/entities/myType/2";
		request.setRequestURI(uri);
		int id=23;
		DerivedType derivedType=new DerivedType();
		derivedType.setOriginalType(originalType);
		TypeAndId typeAndId= new TypeAndId(derivedType, id, jsonType, id);
		
		Mockito.when(refResolver.parseSingleUri(uri)).thenReturn(typeAndId);
		StatefulUpdateService updateService=Mockito.mock(StatefulUpdateService.class);
		
		
		Mockito.when(originalType.getService(StatefulUpdateService.class)).thenReturn(updateService);
		Mockito.when(originalType.getService(ValidationService.class)).thenReturn(null);
		Mockito.when(jsonType.getService(ValidationService.class)).thenReturn(null);
		
		Mockito.when(updateService.update(Mockito.any(Serializable.class), Mockito.any(EntityType.class), Mockito.any(UpdateCallback.class))).thenReturn(null);
		
		
		entityRestService.doPut(request, response);

		ArgumentCaptor<Serializable> idCaptor = ArgumentCaptor.forClass(Serializable.class);
		ArgumentCaptor<EntityType> typeCaptor = ArgumentCaptor.forClass(EntityType.class);
		Mockito.verify(updateService).update(idCaptor.capture(), typeCaptor.capture(), Mockito.any(UpdateCallback.class));
		Assert.assertEquals(id, idCaptor.getValue());
		Assert.assertEquals(originalType, typeCaptor.getValue());
		
		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals("",new String(response.getContentAsByteArray(),"UTF-8"));
	}
	
	@Test
	public void testGetSingle() throws ServletException, IOException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		entityRestService= new EntityRestService();
		final RefResolver refResolver = Mockito.mock(RefResolver.class);
		ReflectionUtils.setField(entityRestService,"refResolver",refResolver);
		final ObjectMapper objectMapper = new ObjectMapper();
		ReflectionUtils.setField(entityRestService,"objectMapper",objectMapper);
		
		
		
		EntityType originalType = Mockito.mock(EntityType.class);
		EntityType jsonType = Mockito.mock(EntityType.class);
		final String uri = "/entity/entities/myType/2";
		request.setRequestURI(uri);
		int id=23;
		DerivedType derivedType=new DerivedType();
		derivedType.setOriginalType(originalType);
		TypeAndId typeAndId= new TypeAndId(derivedType, id, jsonType, id);
		
		Mockito.when(refResolver.parseUri(uri)).thenReturn(typeAndId);
		FindByIdService findByIdService=Mockito.mock(FindByIdService.class);
		Mockito.when(originalType.getService(FindByIdService.class)).thenReturn(findByIdService);
		
		
		
		
		Mockito.when(findByIdService.findById( Mockito.any(EntityType.class), Mockito.any(Serializable.class),Mockito.any(SingleCallback.class))).thenReturn(objectMapper.createObjectNode());
		
		entityRestService.doGet(request, response);

		ArgumentCaptor<Serializable> idCaptor = ArgumentCaptor.forClass(Serializable.class);
		ArgumentCaptor<EntityType> typeCaptor = ArgumentCaptor.forClass(EntityType.class);
		Mockito.verify(findByIdService).findById( typeCaptor.capture(), idCaptor.capture(),Mockito.any(SingleCallback.class));
		Assert.assertEquals(id, idCaptor.getValue());
		Assert.assertEquals(originalType, typeCaptor.getValue());
		
		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals("{}",new String(response.getContentAsByteArray(),"UTF-8"));
	}
		
	@Test
	public <O,T> void testGetCollection() throws ServletException, IOException {
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		entityRestService= new EntityRestService();
		final RefResolver refResolver = Mockito.mock(RefResolver.class);
		ReflectionUtils.setField(entityRestService,"refResolver",refResolver);
		final ObjectMapper objectMapper = new ObjectMapper();
		ReflectionUtils.setField(entityRestService,"objectMapper",objectMapper);
		
		
		
		EntityType<O> originalType = Mockito.mock(EntityType.class);
		EntityType<T> jsonType = Mockito.mock(EntityType.class);
		DerivedType<O,T> derivedType=new DerivedType();
		derivedType.setOriginalType(originalType);
		CollectionResource<O, T> collectionResource = new CollectionResource<O, T>(derivedType, jsonType);
		final String uri = "/entity/entities/myType";
		request.setRequestURI(uri);
		derivedType.setOriginalType(originalType);
		CollectionResource resource= new CollectionResource(derivedType, jsonType);
		
		Mockito.when(refResolver.parseUri(uri)).thenReturn(resource);
		GetCollectionService getCollectionService=Mockito.mock(GetCollectionService.class);
		Mockito.when(originalType.getService(GetCollectionService.class)).thenReturn(getCollectionService);
		
		
		
		
		
		entityRestService.doGet(request, response);
		
		Assert.assertEquals(200, response.getStatus());
		System.out.println(response.getContentAsString());
	}
		
	
	@Test
	public <O>void testCreate() throws IOException
	{
		MockHttpServletRequest request = new MockHttpServletRequest();
		MockHttpServletResponse response = new MockHttpServletResponse();
		entityRestService= new EntityRestService();
		final RefResolver refResolver = context.mock(RefResolver.class);
		ReflectionUtils.setField(entityRestService,"refResolver",refResolver);
		final ObjectMapper objectMapper = new ObjectMapper();
		ReflectionUtils.setField(entityRestService,"objectMapper",objectMapper);
		
		request.setContent("{}".getBytes());
		
		final String uri = "/entity/entities/myType";
		request.setRequestURI(uri);
		final EntityType<O> originalType= context.mock(EntityType.class,"originalType");
		final EntityType<ObjectNode> entityType= context.mock(EntityType.class);
		DerivedType<O,ObjectNode> derivedType=new DerivedType<O,ObjectNode>();
		derivedType.setOriginalType(originalType);
		final CollectionResource<O,ObjectNode> resource=new CollectionResource<O,ObjectNode>(derivedType,entityType);
		final InsertionService persistenceService = context.mock(InsertionService.class);
		
		
		
		context.checking(new Expectations(){{
			one(refResolver).parseCollectionUri(uri);
			will(returnValue(resource));
			
			one(refResolver).in(resource.getEntityType(),objectMapper.createObjectNode());
			O entity = (O) new Object();
			will(returnValue(entity));
			
			one(entityType).getService(ValidationService.class);
			will(returnValue(null));
			
			one(originalType).getService(InsertionService.class);
			will(returnValue(persistenceService));
			
			one(persistenceService).insert(with(any(EntityType.class)), with(any(InsertionCallback.class)));
			will(returnValue(12));
			
		}});
		entityRestService.doPost(request, response);
		
		Assert.assertEquals(200, response.getStatus());
		Assert.assertEquals("12",new String(response.getContentAsByteArray(),"UTF-8"));

	}
	
	


}
