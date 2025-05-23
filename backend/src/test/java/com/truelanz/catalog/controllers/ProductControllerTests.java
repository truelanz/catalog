package com.truelanz.catalog.controllers;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.truelanz.catalog.dto.ProductDTO;
import com.truelanz.catalog.services.ProductService;
import com.truelanz.catalog.services.exceptions.DataBaseException;
import com.truelanz.catalog.services.exceptions.ResourceNotFoundException;
import com.truelanz.catalog.tests.Factory;

@WebMvcTest(value = ProductController.class, excludeAutoConfiguration = {SecurityAutoConfiguration.class}) //Testar apenas o controller, excluindo a configuração de segurança
public class ProductControllerTests {
    
    @Autowired
    private MockMvc mockMvc; //Fazer teste de requisição web.

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    private long existingId;
    private long nonExistingId;
    private long dependentId;
    private ProductDTO productDto;
    private Page<ProductDTO> page;
    private String jsonBody;

    @BeforeEach //Executar antes de cada teste
    void setUp() throws Exception {
        existingId = 1L;
        nonExistingId = 100L;
        dependentId = 3L;
        productDto = Factory.createProductDTO();
        page = new PageImpl<>(List.of(productDto));
        //Converter obj Java em String
        jsonBody = objectMapper.writeValueAsString(productDto);

        Mockito.when(productService.findAllPaged((Pageable)ArgumentMatchers.any())).thenReturn(page);

        Mockito.when(productService.findById(existingId)).thenReturn(productDto);
        Mockito.when(productService.findById(nonExistingId)).thenThrow(ResourceNotFoundException.class);

        Mockito.when(productService.update(ArgumentMatchers.eq(existingId), ArgumentMatchers.any())).thenReturn(productDto);
        Mockito.when(productService.update(ArgumentMatchers.eq(nonExistingId), ArgumentMatchers.any())).thenThrow(ResourceNotFoundException.class);

        Mockito.when(productService.insert(ArgumentMatchers.any())).thenReturn(productDto);

        Mockito.doNothing().when(productService).delete(existingId);
        Mockito.doThrow(ResourceNotFoundException.class).when(productService).delete(nonExistingId);
        Mockito.doThrow(DataBaseException.class).when(productService).delete(dependentId);
    }

    @Test // Find All return a page
    public void findAllShouldReturnPage() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products")
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test //Find By ID when exists
    public void findByIdShouldReturnProductWhenIdExists() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", existingId)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists());
    }

    @Test //Find By ID when NOT exists
    public void findByIdShouldThrowsResourceNotFoundWhenIdDoesNotExists () throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.get("/products/{id}", nonExistingId)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test // Update projectDTO when Id exists
    public void updateShouldReturnProductDTOWhenIdExists() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", existingId)
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isOk())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists());
    }

    @Test // Update Return Not Found when Id NOT exists
    public void updateShouldThrowsResourceNotFoundWhenIdDoesNotExists() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.put("/products/{id}", nonExistingId)
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test // Insert created a productDto
    public void insertShouldReturnProductDto() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders.post("/products")
            .content(jsonBody)
            .contentType(MediaType.APPLICATION_JSON)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isCreated())
            .andExpect(MockMvcResultMatchers.jsonPath("$.id").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.name").exists())
            .andExpect(MockMvcResultMatchers.jsonPath("$.description").exists())
            .andDo(MockMvcResultHandlers.print());
    }


    @Test // Delete Do Nothing when Id exists
    public void deleteShouldReturnNothingWhenIdExists() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", existingId)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNoContent());
    }

    @Test // Delete Do throw Resource Not Found when Id does not exists
    public void deleteShouldThrowsResourceNotFoundExceptionWhenIdDoesNotExists() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", nonExistingId)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isNotFound());
    }

    @Test // Delete Do throw Data Base Exception when Id dependent exists
    public void deleteShouldThrowsDataBaseExceptionWhenIdDependentExists() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/products/{id}", dependentId)
            .accept(MediaType.APPLICATION_JSON))
            .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }
}
