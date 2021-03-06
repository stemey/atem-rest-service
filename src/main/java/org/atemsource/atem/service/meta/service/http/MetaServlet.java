package org.atemsource.atem.service.meta.service.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atemsource.atem.api.BeanLocator;
import org.atemsource.atem.service.meta.service.Cors;

public class MetaServlet extends HttpServlet{

	@Override
	protected void doOptions(HttpServletRequest arg0, HttpServletResponse arg1)
			throws ServletException, IOException {
		new Cors().appendCors(arg1);
		super.doOptions(arg0, arg1);
	}

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		BeanLocator.getInstance().getInstance(MetaRestService.class).doGet(req, resp);
	}
	
	

}
