package org.atemsource.atem.service.entity.collection;

import javax.servlet.http.HttpServletRequest;

import org.atemsource.atem.service.entity.search.Query;
import org.atemsource.atem.service.refresolver.CollectionResource;

public interface QueryParser<O,T> {

	 Query parseQuery(HttpServletRequest req, CollectionResource<O,T> resource);

}