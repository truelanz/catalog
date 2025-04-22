package com.truelanz.catalog.services;

import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.truelanz.catalog.dto.ProductDTO;
import com.truelanz.catalog.entities.Category;
import com.truelanz.catalog.entities.Product;
import com.truelanz.catalog.repositories.CategoryRepository;
import com.truelanz.catalog.repositories.ProductRepository;
import com.truelanz.catalog.services.exceptions.DataBaseException;
import com.truelanz.catalog.services.exceptions.ResourceNotFoundException;
import com.truelanz.catalog.tests.Factory;

import jakarta.persistence.EntityNotFoundException;

@ExtendWith(SpringExtension.class) //Teste de Unidade (component)
public class ProductServiceTests {

    @InjectMocks
    private ProductService productService;
    
    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private PageImpl<Product> page;
    private Product product;
    private ProductDTO productDto;
    private Category category;

    @BeforeEach //Executar antes de cada teste
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 100L;
        dependentId = 3L;
        product = Factory.createProduct();
        productDto = Factory.createProductDTO();
        category = Factory.createCategory();
        page = new PageImpl<>(List.of(product));

        // Simulando comportamento do repository
        Mockito.when(productRepository.existsById(existingId)).thenReturn(true);
        Mockito.when(productRepository.existsById(nonExistingId)).thenReturn(false);
        Mockito.when(productRepository.existsById(dependentId)).thenReturn(true);

        Mockito.doThrow(EmptyResultDataAccessException.class).when(productRepository).deleteById(nonExistingId);
        Mockito.doThrow(DataIntegrityViolationException.class).when(productRepository).deleteById(dependentId);

        Mockito.when(productRepository.findAll((Pageable)ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(productRepository.save(ArgumentMatchers.any())).thenReturn(product);

        Mockito.when(productRepository.findById(existingId)).thenReturn(Optional.of(product));
        Mockito.when(productRepository.findById(nonExistingId)).thenReturn(Optional.empty());
        Mockito.when(categoryRepository.findById(existingId)).thenReturn(Optional.of(category));

        Mockito.when(productRepository.getReferenceById(existingId)).thenReturn(product);
        Mockito.when(productRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
        
        Mockito.when(categoryRepository.getReferenceById(existingId)).thenReturn(category);
        Mockito.when(categoryRepository.getReferenceById(nonExistingId)).thenThrow(EntityNotFoundException.class);
    }

    @Test
    public void deleteShouldDoNothingWhenIdExistis() {

        Assertions.assertDoesNotThrow(() -> {
            productService.delete(existingId);
        });
    }

    @Test
    public void deleteShouldThrowResourceNotFoundExceptionWhenIdDoesNotExistis() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.delete(nonExistingId);
        });
    }

    @Test
    public void deleteShouldThrowDataBaseExceptionWhenDependentId() {

        Assertions.assertThrows(DataBaseException.class, () -> {
            productService.delete(dependentId);
        });
    }

    @Test
    public void findAllShouldReturnPage() {

        Pageable pageable = PageRequest.of(0,10);

        Page<ProductDTO> result = productService.findAllPaged(pageable);
        Assertions.assertNotNull(result);
        Mockito.verify(productRepository).findAll(pageable);
    }

    @Test 
    public void findByIdShouldReturnProductDTOWhenIdExists() {

        ProductDTO dto = productService.findById(existingId);
        Assertions.assertNotNull(dto);
    }

    @Test
    public void findByIdShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.findById(nonExistingId);
        });
    }
        
    @Test
    public void updateShouldReturnProductDTOWhenIdExists() {

        ProductDTO result = productService.update(existingId, productDto);
        Assertions.assertNotNull(result);

    }

    @Test
    public void updateShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExists() {

        Assertions.assertThrows(ResourceNotFoundException.class, () -> {
            productService.update(nonExistingId, productDto);
        });
    }
}

