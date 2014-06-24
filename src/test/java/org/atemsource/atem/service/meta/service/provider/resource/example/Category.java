package org.atemsource.atem.service.meta.service.provider.resource.example;

import java.util.List;

import org.atemsource.atem.api.attribute.annotation.Association;

public class Category {
	@Association(targetType = Product.class)
	private List<Product> products;
	private int id;
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	private String name;

	public List<Product> getProducts() {
		return products;
	}

	public void setProducts(List<Product> products) {
		this.products = products;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
}
