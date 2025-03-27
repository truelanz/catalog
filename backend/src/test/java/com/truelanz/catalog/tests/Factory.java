package com.truelanz.catalog.tests;

import java.time.Instant;

import com.truelanz.catalog.dto.ProductDTO;
import com.truelanz.catalog.entities.Category;
import com.truelanz.catalog.entities.Product;

public class Factory {

    public static Product createProduct() {
        Product product = new Product(
        1L, "Phone", "Good Phone", 800d,
        "https://img.com/img.png", Instant.parse("2025-10-20T03:00:00z")
        );
        product.getCategories().add(new Category(2L, "Electronics"));
        return product;
    }

    public static ProductDTO createProductDTO() {
        Product product = createProduct();
        return new ProductDTO(product, product.getCategories());
    }    
}
