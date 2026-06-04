package com.prabu.ecommerce.service;

import com.prabu.ecommerce.dto.requestdto.BrandRequestDTO;
import com.prabu.ecommerce.dto.requestdto.CategoryRequestDTO;
import com.prabu.ecommerce.dto.responsedto.ProductResponseDTO;
import com.prabu.ecommerce.model.Brand;
import com.prabu.ecommerce.model.Category;
import com.prabu.ecommerce.model.Product;

import java.util.List;

public interface CategoryService {
    boolean addCategory(CategoryRequestDTO categoryRequest);

    boolean updateCategory(Long id, CategoryRequestDTO categoryRequest);

    boolean deleteCategory(Long id);

    List<Category> getAllCategories();

    List<ProductResponseDTO> getProductsByCategory(Long categoryId);

    ProductResponseDTO convertToResponseDTO(Product product);

    boolean addBrand(BrandRequestDTO brandRequest);

    boolean updateBrand(Long id, BrandRequestDTO brandRequest);

    boolean deleteBrand(Long id);

    List<Brand> getAllBrands();
}
