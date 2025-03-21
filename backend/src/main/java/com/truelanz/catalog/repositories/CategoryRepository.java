package com.truelanz.catalog.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.truelanz.catalog.dto.CategoryDTO;
import com.truelanz.catalog.entities.Category;
import java.util.List;


@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    
}
