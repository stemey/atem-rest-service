package org.atemsource.atem.service.entity;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Serializable;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.service.DeletionService;
import org.atemsource.atem.api.service.InsertionCallback;
import org.atemsource.atem.api.service.InsertionService;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.meta.service.Cors;
import org.atemsource.atem.service.refresolver.CollectionResource;
import org.atemsource.atem.service.refresolver.RefResolver;
import org.atemsource.atem.utility.transform.api.ConverterFactory;
import org.atemsource.atem.utility.transform.api.JacksonTransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.atemsource.atem.utility.validation.SimpleValidationContext;
import org.atemsource.atem.utility.validation.ValidationService;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

/**
 * This class provides CRUD operations on entities
 * 
 * @author stemey
 * 
 */
public class EntityRestService {


	private RefResolver refResolver;

	private Cors cors = new Cors();

	public void setRefResolver(RefResolver refResolver) {
		this.refResolver = refResolver;
	}

	public static void setLogger(Logger logger) {
		EntityRestService.logger = logger;
	}

	public void setEntityTypeRepository(EntityTypeRepository entityTypeRepository) {
		this.entityTypeRepository = entityTypeRepository;
	}

	private static Logger logger = Logger.getLogger(EntityRestService.class);

	private ConverterFactory idConverterfactory;
	

	private ObjectMapper objectMapper;

	@Inject
	private EntityTypeRepository entityTypeRepository;

	/**
	 * insert a new entity
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public <O> void doPut(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		cors.appendCors(resp);
		try {
			String uri = req.getRequestURI();
			TypeAndId<O,ObjectNode> typeAndId = refResolver.parseSingleUri(uri);
			if (typeAndId != null) {
				BufferedReader reader = req.getReader();
				ObjectNode jsonNode = (ObjectNode) objectMapper
						.readTree(reader);
				updateEntity(typeAndId, jsonNode);
			} else {
				// 404
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			handle500Error(resp, e);
		}

	}

	/**
	 * update an existing entity
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		cors.appendCors(resp);
		try {
			String uri = req.getRequestURI();
			CollectionResource<?,ObjectNode> collectionResource = refResolver
					.parseCollectionUri(uri);
			if (collectionResource != null) {
				BufferedReader reader = req.getReader();
				ObjectNode jsonNode = (ObjectNode) objectMapper.readTree(reader);
				Object returnValue = createEntity(resp,collectionResource, jsonNode);
				objectMapper.writeValue(resp.getWriter(), returnValue);
			} else {
				// 404
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			handle500Error(resp, e);
		}

	}

	/**
	 * get a sngle entity or a collection
	 * 
	 * @param req
	 * @param resp
	 * @throws ServletException
	 * @throws IOException
	 */
	public <O,T> void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws  IOException {
		cors.appendCors(resp);
		String uri = req.getRequestURI();
		try {
			CollectionResource<O,T> resource = refResolver.parseUri(uri);
			try {
				if (resource instanceof TypeAndId) {
					TypeAndId<O,T> typeAndId=(TypeAndId<O, T>) resource;	
					T entity = readEntity(typeAndId);
					if (entity == null) {
						// 404
						resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
					} else {
						objectMapper.writeValue(resp.getWriter(), entity);
					}

				} else {
					GetCollectionService<O,T> getCollectionService = resource.getOriginalType().getService(GetCollectionService.class);
					getCollectionService.serveCollection(resource, req, resp);
				}

			} catch (Exception e) {
				handle500Error(resp, e);
			}
		} catch (Exception e) {
			logger.error("cannot find resource", e);
			resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
		}

	}

	
	
	/**
	 * delete an entity
	 * 
	 * @param req
	 * @param resp
	 * @throws IOException
	 */
	public <O,T> void doDelete(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		cors.appendCors(resp);
		try {
			String uri = req.getRequestURI();
			TypeAndId<O,T> typeAndId = refResolver.parseSingleUri(uri);
			if (typeAndId != null) {
				if (typeAndId.getOriginalId() == null) {
					// 404 
					resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
				} else {
					// TODO errors should be possible too
					deleteEntity(typeAndId.getId(), typeAndId.getOriginalType());

				}
			} else {
				// 404
				resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
			}
		} catch (Exception e) {
			handle500Error(resp, e);
		}
	}

	/**
	 * 
	 * @param entityType
	 * @return the collection uri for the given type
	 */
	public String getCollectionUri(EntityType<?> entityType) {
		return refResolver.getCollectionUri(entityType);
	}

	/**
	 * get the converter for ids
	 * 
	 * @return
	 */
	public ConverterFactory getIdConverterfactory() {
		return idConverterfactory;
	}

	/**
	 * get uri by id
	 * 
	 * @param entityType
	 * @param id
	 * @return
	 */
	public String getUriById(EntityType<?> entityType, Serializable id) {
		return refResolver.getSingleUri(entityType, String.valueOf(id));
	}

	/**
	 * get entity type from single or collection uri
	 * 
	 * @param url
	 * @return
	 */
	public EntityType<?> getEntityTypeForUri(String url) {
		return refResolver.parseSingleUri(url).getEntityType();
	}

	public void setIdConverterfactory(ConverterFactory idConverterfactory) {
		this.idConverterfactory = idConverterfactory;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}



	private ObjectNode createErrorNode(ReturnErrorObject returnErrorObject) {
		ObjectNode error = objectMapper.createObjectNode();
		ArrayNode errors = objectMapper.createArrayNode();
		error.put("errors", errors);
		for (org.atemsource.atem.utility.validation.AbstractValidationContext.Error e : returnErrorObject
				.getErrors()) {
			ObjectNode singleError = objectMapper.createObjectNode();
			singleError.put("path", e.getPath());
			singleError.put("message", e.getMessage());
			errors.add(singleError);
		}
		return error;
	}

	public static class Result {
		public ArrayNode entities;

		public long totalCount;
	}

	
	
	

	
	

	private <O,T >T readEntity(final TypeAndId<O,T> typeAndId) {
		FindByIdService findByIdService = typeAndId.getOriginalType()
				.getService(FindByIdService.class);

		T json = findByIdService.findById(typeAndId.getOriginalType(), typeAndId.getOriginalId(),
				new SingleCallback<O,T>() {

					@Override
					public T process(O entity) {
						UniTransformation<O, T> ab = (UniTransformation<O, T>) typeAndId
								.getTransformation().getAB();
						return ab.convert(entity,
								new JacksonTransformationContext(
										entityTypeRepository));
					}
				});
		return json;
	}

	private <O,T>  ObjectNode updateEntity(final TypeAndId<O,T> typeAndId,
			final T updatedObject) {
		SimpleValidationContext context = new SimpleValidationContext(
				entityTypeRepository);
		ValidationService validationService = typeAndId.getEntityType()
				.getService(ValidationService.class);
		if (validationService != null) {
			validationService.validate(typeAndId.getEntityType(), context,
					updatedObject);
			if (context.getErrors().size() > 0) {
				ReturnErrorObject returnErrorObject = new ReturnErrorObject();
				returnErrorObject.setErrors(context.getErrors());
				return createErrorNode(returnErrorObject);
			}
		}
		StatefulUpdateService crudService = typeAndId.getOriginalType()
				.getService(StatefulUpdateService.class);

		ReturnErrorObject returnErrorObject = crudService.update(
				typeAndId.getOriginalId(), typeAndId.getOriginalType(),
				new UpdateCallback<O>() {

					@Override
					public ReturnErrorObject update(O entity) {
						refResolver.mergeIn(typeAndId.getEntityType(), entity, updatedObject);
						
						EntityType<O> originalType = typeAndId
								.getOriginalType();
						ValidationService validationService = originalType
								.getService(ValidationService.class);
						if (validationService != null) {
							SimpleValidationContext context = new SimpleValidationContext(
									entityTypeRepository);
							validationService.validate(originalType, context,
									entity);
							if (context.getErrors().size() > 0) {
								ReturnErrorObject returnErrorObject = new ReturnErrorObject();
								returnErrorObject.setErrors(context.getErrors());
								return returnErrorObject;
							}
						}
						return null;
					}
				});
		if (returnErrorObject == null) {
			return null;
		} else {
			return createErrorNode(returnErrorObject);
		}
	}


	
	private <O> Object createEntity(HttpServletResponse response,final CollectionResource<O,ObjectNode> resource,
			final ObjectNode entity) {
		SimpleValidationContext context = new SimpleValidationContext(
				entityTypeRepository);
		ValidationService validationService = resource.getEntityType()
				.getService(ValidationService.class);
		if (validationService != null) {
			validationService.validate(resource.getEntityType(), context,
					entity);
			if (context.getErrors().size() > 0) {
				response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
				ReturnErrorObject returnErrorObject = new ReturnErrorObject();
				returnErrorObject.setErrors(context.getErrors());
				return createErrorNode(returnErrorObject);
			}
		}

		InsertionService insertionService = resource.getOriginalType()
				.getService(InsertionService.class);

		
		Serializable id = insertionService.insert(resource.getOriginalType(),
				new InsertionCallback<O>() {

					@Override
					public O get() {
						return refResolver.in(resource.getEntityType(), entity);
					}
				}
			
		);

		return id;
	}

	private void handle500Error(HttpServletResponse resp, Exception e)
			throws IOException {
		logger.error("error when serving request", e);
		resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		//e.printStackTrace(resp.getWriter());
		resp.flushBuffer();
	}

	private void deleteEntity(Serializable id, EntityType<?> entityType) {
		DeletionService deletionService = entityType
				.getService(DeletionService.class);
		if (deletionService != null) {
			deletionService.delete(entityType, id);
		}
	}

}
