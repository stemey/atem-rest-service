package org.atemsource.atem.service.entity.collection;

import java.io.IOException;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atemsource.atem.api.EntityTypeRepository;
import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.entity.EntityRestService.Result;
import org.atemsource.atem.service.entity.FindByTypeService;
import org.atemsource.atem.service.entity.GetCollectionService;
import org.atemsource.atem.service.entity.ListCallback;
import org.atemsource.atem.service.entity.search.Paging;
import org.atemsource.atem.service.entity.search.Query;
import org.atemsource.atem.service.entity.search.Sorting;
import org.atemsource.atem.service.refresolver.CollectionResource;
import org.atemsource.atem.utility.transform.api.JacksonTransformationContext;
import org.atemsource.atem.utility.transform.api.UniTransformation;
import org.codehaus.jackson.map.ObjectMapper;
import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

public class JpaGetCollectionService<O> implements
		GetCollectionService<O, ObjectNode> {

	private PagingManager<O, ObjectNode> pagingManager;

	private SortingParser<O, ObjectNode> sortingParser;

	public void setPagingManager(PagingManager<O, ObjectNode> pagingManager) {
		this.pagingManager = pagingManager;
	}

	public void setSortingParser(SortingParser<O, ObjectNode> sortingParser) {
		this.sortingParser = sortingParser;
	}

	public void setObjectMapper(ObjectMapper objectMapper) {
		this.objectMapper = objectMapper;
	}

	public void setParser(QueryParser<O, ObjectNode> parser) {
		this.parser = parser;
	}

	public void setEntityTypeRepository(
			EntityTypeRepository entityTypeRepository) {
		this.entityTypeRepository = entityTypeRepository;
	}

	private ObjectMapper objectMapper;

	private QueryParser<O, ObjectNode> parser;

	private EntityTypeRepository entityTypeRepository;

	@Override
	public void serveCollection(CollectionResource<O, ObjectNode> resource,
			HttpServletRequest request, HttpServletResponse response)
			throws IOException {
		Query query = parser.parseQuery(request, resource);
		Paging paging = pagingManager.parsePaging(request, resource);
		Sorting sorting = sortingParser.parseSorting(request, resource);
		Result result = readEntities(resource, query, sorting, paging);
		pagingManager.addContentRange(response, paging, result);
		objectMapper.writeValue(response.getWriter(), result.entities);

	}

	private Result readEntities(
			final CollectionResource<O, ObjectNode> resource, Query query,
			Sorting sorting, Paging paging) {
		EntityType<O> originalType = resource.getOriginalType();
		FindByTypeService findByTypeService = originalType
				.getService(FindByTypeService.class);
		Result result = findByTypeService.getEntities(originalType, query,
				sorting, paging, new ListCallback<O>() {

					@Override
					public Result process(List<O> entities, long totalCount) {
						UniTransformation<O, ObjectNode> ab = (UniTransformation<O, ObjectNode>) resource
								.getTransformation().getAB();
						Result result = new Result();
						ArrayNode arrayNode = objectMapper.createArrayNode();
						for (O entity : entities) {
							try {
							ObjectNode json = ab.convert(entity,
									new JacksonTransformationContext(
											entityTypeRepository));
							arrayNode.add(json);
							}catch(Exception e) {
									
							}
						}
						result.entities = arrayNode;
						result.totalCount = totalCount;
						return result;
					}
				});
		return result;
	}
}
