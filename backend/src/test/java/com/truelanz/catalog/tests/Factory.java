package com.truelanz.catalog.tests;

import java.time.Instant;

import com.truelanz.catalog.dto.ProductDTO;
import com.truelanz.catalog.entities.Category;
import com.truelanz.catalog.entities.Product;

public class Factory {

    public static Product createProduct() {
        Product product = new Product(
        1L, "Phone", "Good Phone", 800d,
        "https://img.com/img.png", Instant.now()
        );
        product.getCategories().add(createCategory());
        return product;
    }

    public static ProductDTO createProductDTO() {
        Product product = createProduct();
        return new ProductDTO(product, product.getCategories());
    }

    public static Category createCategory() {
        return new Category(1L, "Electronics");
    }
}
