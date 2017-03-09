package com.serverless.products.model;

public class AddProductRequest {

	private Product product;

	public AddProductRequest() {
	}

	public Product getProduct() {
		return product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	@Override
	public String toString() {
		return "AddProductRequest [product=" + product + "]";
	}
	
}
