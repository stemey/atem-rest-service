package org.atemsource.atem.service.entity;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.service.meta.service.Cors;

public class EntityServlet extends HttpServlet{

	
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BeanLocator.getInstance().getInstance(EntityRestService.class).doGet(req, resp);
	}
	@Override
	protected void doOptions(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		Cors cors = new Cors();
		cors.appendCors(arg1);
		super.doOptions(arg0, arg1);
	}
	@Override
	protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BeanLocator.getInstance().getInstance(EntityRestService.class).doPut(req, resp);
	}
	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BeanLocator.getInstance().getInstance(EntityRestService.class).doPost(req, resp);
	}
	@Override
	protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BeanLocator.getInstance().getInstance(EntityRestService.class).doDelete(req, resp);
	}

}
