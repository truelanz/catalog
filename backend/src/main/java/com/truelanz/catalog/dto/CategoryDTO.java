package com.truelanz.catalog.dto;

import com.truelanz.catalog.entities.Category;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class CategoryDTO {
    
    private Long id;
    @NotBlank(message = "Field required")
    @Size(min = 3, max = 80, message = "field must be 3 to 80 characters long")
    private String name;

    public CategoryDTO(Category entity) {
        this.id = entity.getId();
        this.name = entity.getName();
    }
}
