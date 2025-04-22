package com.truelanz.catalog.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.truelanz.catalog.dto.ProductDTO;
import com.truelanz.catalog.tests.Factory;

@SpringBootTest
@AutoConfigureMockMvc
@Transactional
public class ProductControllerIntegration {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    private Long existingId;
    private Long nonExistingId;
    private Long countToltalProducts;

    @BeforeEach
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 100L;
        countToltalProducts = 25L;

    }

    @Test //findAll need to return Sort By name request and total elements
    public void findAllShouldReturnSortedPageWhenSortByName() throws Exception{

        mockMvc.perform(MockMvcRequestBuilders.get("/products?page=0&size=12&sort=name,asc")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name").value("Macbook Pro"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.content[1].name").value("PC Gamer"))
            .andExpect(MockMvcResultMatchers.jsonPath("$.totalElements").value(countToltalProducts));
    }
    
    @Test //update need return product DTO when Id exists
    public void updateShoulddReturnProductDtoWhenIdExists() throws Exception{

        ProductDTO productDTO = Factory.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        String expectedName = productDTO.getName();
        String expectedDescription = productDTO.getDescription();

        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", existingId)
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(existingId))
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").value(expectedName))
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").value(expectedDescription));
    }

    @Test //update need return product DTO when Id exists
    public void updateShouldThrowsResourceNotFoundWhenIdDoesNotExists() throws Exception{

        ProductDTO productDTO = Factory.createProductDTO();
        String jsonBody = objectMapper.writeValueAsString(productDTO);

        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", nonExistingId)
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }
}
