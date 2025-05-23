package com.truelanz.catalog.repositories;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.truelanz.catalog.entities.Product;
import com.truelanz.catalog.projections.ProductProjection;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query(nativeQuery = true, value = """
        SELECT DISTINCT p.id, p.name 
        FROM tb_product p
        INNER JOIN tb_product_category pc ON p.id = pc.product_id
        WHERE (:categoryIds IS NULL OR pc.category_id IN :categoryIds)
        AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
        ORDER BY p.name
        """, countQuery = """
        SELECT COUNT(*) FROM (
            SELECT DISTINCT p.id, p.name 
            FROM tb_product p
            INNER JOIN tb_product_category pc ON p.id = pc.product_id
            WHERE (:categoryIds IS NULL OR pc.category_id IN :categoryIds)
            AND LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%'))
        ) AS tb_result
    """)
    Page<ProductProjection> searchProducts(List<Long> categoryIds, String name, Pageable pageable);

    @Query("SELECT obj FROM Product obj JOIN FETCH obj.categories "
		+ "WHERE obj.id IN :productIds ORDER BY obj.name")
        List<Product> searchProductsWithCategories(List<Long> productIds);
    }
