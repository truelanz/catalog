package com.truelanz.catalog.services;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import com.truelanz.catalog.dto.ProductDTO;
import com.truelanz.catalog.repositories.ProductRepository;
import com.truelanz.catalog.services.exceptions.ResourceNotFoundException;

import jakarta.transaction.Transactional;

@SpringBootTest
@Transactional
public class ProductServiceIntegration {

    @Autowired
    private ProductService productService;

    @Autowired
    private ProductRepository productRepository;

    private Long existingId;
    private Long nonExistingId;
    private Long countToltalProducts;
    /* private Product product;
    private ProductDTO productDto;
    private PageImpl<Product> page; */

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 100L;
        countToltalProducts = 25L;
        /* product = Factory.createProduct();
        productDto = Factory.createProductDTO();
        page = new PageImpl<>(List.of(product)); */

    }

    @Test //delete does not throw and -1 element when id exists
    public void deleteShouldDeleteResourceWhenIdExists() {

        //verificar se não deu erro
        Assertions.assertDoesNotThrow(() -> {
            productService.delete(existingId);
        });

        //verificar se diminuiu 1 produto do registro
        Assertions.assertEquals(countToltalProducts - 1, productRepository.count());
    }

    @Test //delete should throw resourse not found when id does not exists
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });
    }

    @Test //findAll return page 0, 10 elements
    public void findAllPagedShouldReturnPageWhenPage0Size10() {

        PageRequest pageRequest = PageRequest.of(0, 10);

        Page<ProductDTO> result = productService.findAllPaged(pageRequest);

        Assertions.assertFalse(result.isEmpty());
        Assertions.assertEquals(0, result.getNumber());
        Assertions.assertEquals(10, result.getSize());
        Assertions.assertEquals(countToltalProducts, result.getTotalElements());
    }

    @Test //findAll return empty if page does not exists
    public void findAllPagedShouldReturnEmptyPageWhenPageDoesNotExists() {

        PageRequest pageRequest = PageRequest.of(50, 10);

        Page<ProductDTO> result = productService.findAllPaged(pageRequest);

        Assertions.assertTrue(result.isEmpty());
    }

    @Test // findAll Pageable sorted by name
    public void findAllPagedShouldReturnSortedPageWhenSortedByName() {

        PageRequest pageRequest = PageRequest.of(0, 10, Sort.by("name"));

        Page<ProductDTO> result = productService.findAllPaged(pageRequest);

        Assertions.assertFalse(result.isEmpty());
        //Macbook Pro -> PC Gamer -> PC Gamer Alfa.
        Assertions.assertEquals("Macbook Pro", result.getContent().get(0).getName());
        Assertions.assertEquals("PC Gamer", result.getContent().get(1).getName());
        Assertions.assertEquals("PC Gamer Alfa", result.getContent().get(2).getName());
    }

}
