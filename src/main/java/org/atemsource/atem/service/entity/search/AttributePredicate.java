package org.atemsource.atem.service.entity.search;

import org.atemsource.atem.api.attribute.relation.SingleAttribute;

public class AttributePredicate<V> {
	public AttributePredicate(SingleAttribute<V> attribute, Operator operator, Object value) {
		super();
		this.attribute = attribute;
		this.operator = operator;
		this.value = value;
	}

	private SingleAttribute<V> attribute;
	private Operator operator;

	public SingleAttribute<V> getAttribute() {
		return attribute;
	}

	public Operator getOperator() {
		return operator;
	}

	public Object getValue() {
		return value;
	}

	private Object value;
}
