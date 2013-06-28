package org.atemsource.atem.service.entity.search;

import java.util.List;

public class Query {

	public Query(boolean or, List<AttributePredicate<?>> predicates) {
		super();
		this.or = or;
		this.predicates = predicates;
	}

	public boolean isOr() {
		return or;
	}

	public List<AttributePredicate<?>> getPredicates() {
		return predicates;
	}

	private boolean or;
	private List<AttributePredicate<?>> predicates;
}
