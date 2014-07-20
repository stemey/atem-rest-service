package org.atemsource.atem.service.meta.service;

import javax.servlet.http.HttpServletResponse;

public class Cors {
	public void appendCors(HttpServletResponse resp) {
		resp.addHeader("Access-Control-Allow-Origin", "*");
		resp.addHeader("Access-Control-Allow-Methods", "GET,PUT,POST,DELETE");
		resp.addHeader("Access-Control-Allow-Headers",
				"Content-Type, X-Requested-With, If-None-Match, X-Range, Range");
		resp.addHeader("Access-Control-Expose-Headers", "Content-Range");
	}
}
