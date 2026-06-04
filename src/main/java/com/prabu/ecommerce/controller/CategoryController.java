package com.prabu.ecommerce.controller;

import com.prabu.ecommerce.dto.requestdto.BrandRequestDTO;
import com.prabu.ecommerce.dto.requestdto.CategoryRequestDTO;
import com.prabu.ecommerce.dto.responsedto.ApiResponse;
import com.prabu.ecommerce.dto.responsedto.ProductResponseDTO;
import com.prabu.ecommerce.model.Brand;
import com.prabu.ecommerce.model.Category;
import com.prabu.ecommerce.service.CategoryService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // --- Category APIs ---

    @PostMapping("/admin/categories")
    public ResponseEntity<ApiResponse<String>> addCategory(@Valid @RequestBody CategoryRequestDTO categoryRequest) {
        categoryService.addCategory(categoryRequest);
        return ResponseEntity.ok(ApiResponse.success("Category added successfully"));
    }

    @GetMapping("/categories")
    public ResponseEntity<ApiResponse<List<Category>>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(ApiResponse.success("Categories fetched successfully", categories));
    }

    @GetMapping("/categories/{id}/products")
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getProductsByCategory(@PathVariable Long id) {
        List<ProductResponseDTO> products = categoryService.getProductsByCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Products fetched successfully", products));
    }

    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<ApiResponse<String>> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO categoryRequest) {
        categoryService.updateCategory(id, categoryRequest);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully"));
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }

    // --- Brand APIs ---

    @PostMapping("/admin/brands")
    public ResponseEntity<ApiResponse<String>> addBrand(@Valid @RequestBody BrandRequestDTO brandRequest) {
        categoryService.addBrand(brandRequest);
        return ResponseEntity.ok(ApiResponse.success("Brand added successfully"));
    }

    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<Brand>>> getAllBrands() {
        List<Brand> brands = categoryService.getAllBrands();
        return ResponseEntity.ok(ApiResponse.success("Brands fetched successfully", brands));
    }

    @PutMapping("/admin/brands/{id}")
    public ResponseEntity<ApiResponse<String>> updateBrand(@PathVariable Long id, @Valid @RequestBody BrandRequestDTO brandRequest) {
        categoryService.updateBrand(id, brandRequest);
        return ResponseEntity.ok(ApiResponse.success("Brand updated successfully"));
    }

    @DeleteMapping("/admin/brands/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBrand(@PathVariable Long id) {
        categoryService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.success("Brand deleted successfully"));
    }
}
