package com.prabu.ecommerce.repository;

import com.prabu.ecommerce.model.Product;
import com.prabu.ecommerce.model.ProductStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product,Long> {
    Optional<Product> findByName(String name);

    boolean existsBySku(String sku);

    boolean existsByCategoryId(Long categoryId);

    List<Product> findByCategoryIdAndStatus(Long categoryId, ProductStatus status);

    boolean existsByBrandId(Long brandId);

    List<Product> findByStatus(ProductStatus status);
}
