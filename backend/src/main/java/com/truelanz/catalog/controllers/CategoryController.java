package com.truelanz.catalog.controllers;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.truelanz.catalog.dto.CategoryDTO;
import com.truelanz.catalog.entities.Category;
import com.truelanz.catalog.services.CategoryService;

import org.springframework.web.bind.annotation.GetMapping;

@RestController
@RequestMapping(value = "/categories")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;
    
    @GetMapping()
    public ResponseEntity<List<CategoryDTO>> findAll() {
        List <CategoryDTO> list = categoryService.findAll();
        return ResponseEntity.ok().body(list);
    }

}
