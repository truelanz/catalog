package com.truelanz.catalog.dto;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.truelanz.catalog.entities.Category;
import com.truelanz.catalog.entities.Product;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ProductDTO {

    private Long id;
    @NotBlank(message = "Require field")
    @Size(min = 3, max = 80, message = "field must be 3 to 80 characters long")
    private String name;
    @Size(min = 10, message = "The field must be at least 10 characters long", max = 2000)
    private String description;
    @NotBlank(message = "Require field")
    @Positive(message = "the field must be positive")
    private Double price;
    @NotBlank(message = "Require field")
    private String imgUrl;
    private Instant date;
    private List<CategoryDTO> categories = new ArrayList<>();

    public ProductDTO(
        Long id,
        @NotBlank(message = "Require field") @Size(min = 3, max = 80, message = "field must be 3 to 80 characters long") String name,
        @Size(min = 10, message = "The field must be at least 10 characters long", max = 2000) String description,
        @NotBlank(message = "Require field") @Positive(message = "the field must be positive") Double price,
        @NotBlank(message = "Require field") String imgUrl, Instant date) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.price = price;
        this.imgUrl = imgUrl;
        this.date = date;
    }

    public ProductDTO(Product entity) {
        id = entity.getId();
        name = entity.getName();
        description = entity.getDescription();
        price = entity.getPrice();
        imgUrl = entity.getImgUrl();
        date = entity.getDate();
    }

    public ProductDTO(Product entity, Set<Category> categories) {
        this(entity);
        categories.forEach(category -> this.categories.add(new CategoryDTO(category)));
    }
}
