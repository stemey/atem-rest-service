package org.atemsource.atem.service.entity.collection;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.atemsource.atem.service.entity.EntityRestService.Result;
import org.atemsource.atem.service.entity.search.Paging;
import org.atemsource.atem.service.refresolver.CollectionResource;

public class ContentHeaderPagingParser<O,T> implements PagingManager<O,T> {

	private static final Pattern CONTENT_RANGE_PATTERN = Pattern
			.compile("items=([0-9]+)-([0-9]+)?");

	private static final String CONTENT_RANGE_HEADER = "Content-Range";
	@Override
	public  Paging parsePaging(HttpServletRequest req,
			CollectionResource<O,T> resource) {
		Paging paging = null;
		String contentRange = req.getHeader("Range");
		if (contentRange != null) {
			Matcher matcher = CONTENT_RANGE_PATTERN.matcher(contentRange);
			if (matcher.find() && matcher.group(1) != null
					&& matcher.group(2) != null) {
				paging = new Paging(Integer.parseInt(matcher.group(1)),
						Integer.parseInt(matcher.group(2)));
			}
		}
		return paging;
	}
	
	@Override
	public void addContentRange(HttpServletResponse response, Paging paging,Result result) {
		int start = paging == null ? 0 : paging.getStart();
		response.setHeader(CONTENT_RANGE_HEADER, "items " + start + "-"
				+ result.entities.size() + "/" + result.totalCount);
	}

}
