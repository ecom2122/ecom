package com.m2gi.ecom.service;

import com.m2gi.ecom.domain.Category;
import com.m2gi.ecom.domain.Product;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link Product}.
 */
public interface ProductService {
    /**
     * Save a product.
     *
     * @param product the entity to save.
     * @return the persisted entity.
     */
    Product save(Product product);

    /**
     * Partially updates a product.
     *
     * @param product the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Product> partialUpdate(Product product);

    /**
     * Get all the products.
     *
     * @return the list of entities.
     */
    List<Product> findAll();

    /**
     * Get products from query.
     *
     * @return the list of entities.
     */
    List<Product> findResearch(String query);

    /**
     * Get products from a category.
     *
     * @return the list of entities.
     */
    List<Product> findCategory(Category cat);

    /**
     * Get all the products with eager load of many-to-many relationships.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Product> findAllWithEagerRelationships(Pageable pageable);

    /**
     * Get the "id" product.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Product> findOne(Long id);

    /**
     * Delete the "id" product.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
