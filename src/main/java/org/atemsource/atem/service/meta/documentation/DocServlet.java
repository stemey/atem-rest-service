package org.atemsource.atem.service.meta.documentation;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atemsource.atem.api.BeanLocator;


public class DocServlet extends HttpServlet
{

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException
	{

		resp.setContentType("text/html;charset=UTF-8");
		BeanLocator.getInstance().getInstance(DocDispatcher.class)
			.getDocumentation(req.getRequestURI(), resp.getWriter());
	}
}
