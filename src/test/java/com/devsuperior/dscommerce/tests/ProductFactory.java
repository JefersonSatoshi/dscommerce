package com.devsuperior.dscommerce.tests;

import com.devsuperior.dscommerce.entities.Category;
import com.devsuperior.dscommerce.entities.Product;

public class ProductFactory {
	
	public static Product createProduct() {
		Category category = CategoryFactory.createCategory();
		Product product = new Product(1L, "PlayStation 5", "Console de ultima geracao", 3999.0, "www.google.com");
		product.getCategories().add(category);
		return product;
	}
	
	public static Product createProduct(String name) {
		Product product = createProduct();
		product.setName(name);
		return product;
	}
}
