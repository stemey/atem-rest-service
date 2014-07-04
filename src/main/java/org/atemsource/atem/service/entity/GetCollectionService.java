package org.atemsource.atem.service.entity;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atemsource.atem.service.refresolver.CollectionResource;

public interface GetCollectionService<O,T> {
	public  void serveCollection(CollectionResource<O, T> resource,
			HttpServletRequest request, HttpServletResponse response) throws IOException;
}
