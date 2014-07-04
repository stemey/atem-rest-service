package org.atemsource.atem.service.entity.collection;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atemsource.atem.api.type.EntityType;
import org.atemsource.atem.service.entity.EntityRestService.Result;
import org.atemsource.atem.service.entity.search.Paging;
import org.atemsource.atem.service.refresolver.CollectionResource;

public interface PagingManager<O,T> {

	void addContentRange(HttpServletResponse response, Paging paging,
			Result result);

	 Paging parsePaging(HttpServletRequest req, CollectionResource<O,T> resource);

}
