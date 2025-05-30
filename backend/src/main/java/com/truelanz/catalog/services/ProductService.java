package com.truelanz.catalog.services;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import com.truelanz.catalog.dto.CategoryDTO;
import com.truelanz.catalog.dto.ProductDTO;
import com.truelanz.catalog.entities.Category;
import com.truelanz.catalog.entities.Product;
import com.truelanz.catalog.projections.ProductProjection;
import com.truelanz.catalog.repositories.CategoryRepository;
import com.truelanz.catalog.repositories.ProductRepository;
import com.truelanz.catalog.services.exceptions.DataBaseException;
import com.truelanz.catalog.services.exceptions.ResourceNotFoundException;
import com.truelanz.catalog.util.Utils;

import jakarta.persistence.EntityNotFoundException;

@Service
public class ProductService {
    
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    // Find all sem retornar categorias
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(Pageable pageable) {
        Page<Product> result = productRepository.findAll(pageable);
        return result.map(x -> new ProductDTO(x));
    }

    //FindAllPaged com @RequestParams e nativeQuery
    @SuppressWarnings("unchecked")
    @Transactional(readOnly = true)
    public Page<ProductDTO> findAllPaged(String name, String categoryId, Pageable pageable) {

        //Converter string de ids para uma Long List
        List<Long> categoryIdList = Arrays.asList();
        //Somente se não houver passado parâmetro de Request, converter String de ids para uma Long List
        if (!"0".equals(categoryId)){
            categoryIdList = Arrays.asList(categoryId.split(",")).stream().map(Long::parseLong).toList();
        }
        Page<ProductProjection> page = productRepository.searchProducts(categoryIdList, name, pageable);
        List<Long> productsIds = page.map(x -> x.getId()).toList();

        List<Product> entities = productRepository.searchProductsWithCategories(productsIds);
        entities = (List<Product>) Utils.replace(page.getContent(), entities); //Gerar nova lista (ordenada)
        
        List<ProductDTO> dtos = entities.stream().map(p -> new ProductDTO(p, p.getCategories())).toList();

        //instanciar productDTO complete como pageable
        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    // Find by Id retornando as categorias
    @Transactional(readOnly = true)
    public ProductDTO findById(Long id) {
        Optional<Product> obj = productRepository.findById(id);
        Product entity = obj.orElseThrow(() -> new ResourceNotFoundException("Resource not found"));
        return new ProductDTO(entity, entity.getCategories()); //retorna também as categorias dos produtos
    }

    @Transactional
    public ProductDTO insert(ProductDTO dto) {
        Product entity = new Product();
        copyDtoToEntity(dto, entity);
        entity = productRepository.save(entity);
        return new ProductDTO(entity);
    }

    @Transactional
    public ProductDTO update(Long id, ProductDTO dto) {
        try {
            Product entity = productRepository.getReferenceById(id);
            copyDtoToEntity(dto, entity);
            entity = productRepository.save(entity);
            return new ProductDTO(entity);
        } catch (EntityNotFoundException e) {
            throw new ResourceNotFoundException("Id" + id + "not found");
        }
    }

    @Transactional(propagation = Propagation.SUPPORTS)
    public void delete(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ResourceNotFoundException("Recurso não encontrado");
        }
        try {
            productRepository.deleteById(id);    		
        }
            catch (DataIntegrityViolationException e) {
                throw new DataBaseException("Falha de integridade referencial");
        }
    }

    //copiando do DTO para a entidade
    private void copyDtoToEntity(ProductDTO dto, Product entity) {
        entity.setName(dto.getName());
        entity.setPrice(dto.getPrice());
        entity.setDescription(dto.getDescription());
        entity.setDate(dto.getDate());
        entity.setImgUrl(dto.getImgUrl());

        //Para vincular uma entidade ao produto
        entity.getCategories().clear();
        for (CategoryDTO categoryDTO : dto.getCategories()) {
            //System.out.println(">> ID da categoria recebida no DTO: " + categoryDTO.getId());
            Category category = categoryRepository.findById(categoryDTO.getId())
                .orElseThrow(() -> new EntityNotFoundException("Category not found"));
            entity.getCategories().add(category);
        }
    }
}