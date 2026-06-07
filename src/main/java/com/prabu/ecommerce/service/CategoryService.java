package com.prabu.ecommerce.service;

import com.prabu.ecommerce.dto.requestdto.BrandRequestDTO;
import com.prabu.ecommerce.dto.requestdto.CategoryRequestDTO;
import com.prabu.ecommerce.dto.responsedto.ProductResponseDTO;
import com.prabu.ecommerce.model.Brand;
import com.prabu.ecommerce.model.Category;
import com.prabu.ecommerce.model.Product;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface CategoryService {
    boolean addCategory(CategoryRequestDTO categoryRequest, MultipartFile image);

    boolean updateCategory(Long id, CategoryRequestDTO categoryRequest, MultipartFile image);

    boolean deleteCategory(Long id);

    List<Category> getAllCategories();

    List<ProductResponseDTO> getProductsByCategory(Long categoryId);

    ProductResponseDTO convertToResponseDTO(Product product);

    boolean addBrand(BrandRequestDTO brandRequest, MultipartFile image);

    boolean updateBrand(Long id, BrandRequestDTO brandRequest, MultipartFile image);

    boolean deleteBrand(Long id);

    List<Brand> getAllBrands();
}
