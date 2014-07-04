package org.atemsource.atem.service.entity.collection;

import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.http.HttpServletRequest;

import org.atemsource.atem.api.attribute.relation.SingleAttribute;
import org.atemsource.atem.api.infrastructure.exception.TechnicalException;
import org.atemsource.atem.service.entity.search.AttributeSorting;
import org.atemsource.atem.service.entity.search.Sorting;
import org.atemsource.atem.service.refresolver.CollectionResource;

public class GridxSortingParser<O, T> implements SortingParser<O, T> {
	private String sortParam = "sortBy";

	private static final Pattern SORTING_PATTERN = Pattern
			.compile("( |\\-)([a-zA-Z0-9]+)");

	@Override
	public Sorting parseSorting(HttpServletRequest request,
			CollectionResource<O, T> resource) {
		Sorting sorting = null;
		String sortingValue = request.getParameter(sortParam);
		List<AttributeSorting> attributeSortings = new LinkedList<AttributeSorting>();
		if (sortingValue != null) {
			Matcher matcher = SORTING_PATTERN.matcher(sortingValue);
			while (matcher.find()) {
				String attributeCode = matcher.group(2);
				String dir = matcher.group(1);
				SingleAttribute<?> attribute = (SingleAttribute<?>) resource
						.getOriginalType().getAttribute(attributeCode);
				if (attribute == null) {
					throw new TechnicalException(
							"cannot find sorting attribute " + attributeCode);
				}
				attributeSortings.add(new AttributeSorting(attribute, !dir
						.equals("-")));
			}
			sorting = new Sorting(attributeSortings);
		}
		return sorting;
	}

}
