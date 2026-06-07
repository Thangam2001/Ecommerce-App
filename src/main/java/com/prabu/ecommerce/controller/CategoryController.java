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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    // --- Category APIs ---

    @PostMapping(value = "/admin/categories", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> addCategory(@Valid @RequestPart("category") CategoryRequestDTO categoryRequest,
                                                           @RequestPart(value = "image", required = false) MultipartFile image) {
        categoryService.addCategory(categoryRequest, image);
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

    @PutMapping(value = "/admin/categories/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> updateCategory(@PathVariable Long id,
                                                              @Valid @RequestPart("category") CategoryRequestDTO categoryRequest,
                                                              @RequestPart(value = "image", required = false) MultipartFile image) {
        categoryService.updateCategory(id, categoryRequest, image);
        return ResponseEntity.ok(ApiResponse.success("Category updated successfully"));
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<ApiResponse<String>> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok(ApiResponse.success("Category deleted successfully"));
    }

    // --- Brand APIs ---

    @PostMapping(value = "/admin/brands", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> addBrand(@Valid @RequestPart("brand") BrandRequestDTO brandRequest,
                                                        @RequestPart(value = "image", required = false) MultipartFile image) {
        categoryService.addBrand(brandRequest, image);
        return ResponseEntity.ok(ApiResponse.success("Brand added successfully"));
    }

    @GetMapping("/brands")
    public ResponseEntity<ApiResponse<List<Brand>>> getAllBrands() {
        List<Brand> brands = categoryService.getAllBrands();
        return ResponseEntity.ok(ApiResponse.success("Brands fetched successfully", brands));
    }

    @PutMapping(value = "/admin/brands/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> updateBrand(@PathVariable Long id,
                                                           @Valid @RequestPart("brand") BrandRequestDTO brandRequest,
                                                           @RequestPart(value = "image", required = false) MultipartFile image) {
        categoryService.updateBrand(id, brandRequest, image);
        return ResponseEntity.ok(ApiResponse.success("Brand updated successfully"));
    }

    @DeleteMapping("/admin/brands/{id}")
    public ResponseEntity<ApiResponse<String>> deleteBrand(@PathVariable Long id) {
        categoryService.deleteBrand(id);
        return ResponseEntity.ok(ApiResponse.success("Brand deleted successfully"));
    }
}
