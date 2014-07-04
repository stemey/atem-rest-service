package org.atemsource.atem.service.entity.collection;

import javax.servlet.http.HttpServletRequest;

import org.atemsource.atem.service.entity.search.Sorting;
import org.atemsource.atem.service.refresolver.CollectionResource;

public interface SortingParser<O, T> {
	Sorting parseSorting(HttpServletRequest request,
			CollectionResource<O, T> resource);
}
