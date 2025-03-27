package com.truelanz.catalog.repositories;

import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import com.truelanz.catalog.entities.Product;
import com.truelanz.catalog.tests.Factory;

@DataJpaTest
public class ProductRepositoriesTests {

    @Autowired
    private ProductRepository productRepository;
    
    private long existingId;
    private long nonExistingId;
    private long countTotalProducts = 25L;

    @BeforeEach //Executar antes de cada teste
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 100L;
    }
    
    @Test
    public void deleteShouldDeteleObjectWhenIdExists() {
        productRepository.deleteById(existingId);
        Optional<Product> result = productRepository.findById(existingId);
        Assertions.assertFalse(result.isPresent());
    }

    @Test
    public void insertShouldInsertObjectWithAutoIncremente() {
        Product product = Factory.createProduct();

        product.setId(null);
        product = productRepository.save(product);
        Optional<Product> result = productRepository.findById(countTotalProducts + 2); //proximo pós saved

        Assertions.assertNotNull(product.getId());
        Assertions.assertEquals(countTotalProducts + 1, product.getId()); //ultimo salvo deve ser igual ultimo product + 1.
        Assertions.assertFalse(result.isPresent()); //proximo pós ultimo salvo não pode estar presente
    }
}
