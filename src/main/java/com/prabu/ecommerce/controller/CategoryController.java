package com.prabu.ecommerce.controller;

import com.prabu.ecommerce.dto.requestdto.BrandRequestDTO;
import com.prabu.ecommerce.dto.requestdto.CategoryRequestDTO;
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
    public ResponseEntity<String> addCategory(@Valid @RequestBody CategoryRequestDTO categoryRequest) {
        categoryService.addCategory(categoryRequest);
        return ResponseEntity.ok("Category added successfully");
    }

    @GetMapping("/categories")
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();
        return ResponseEntity.ok(categories);
    }

    @GetMapping("/categories/{id}/products")
    public ResponseEntity<List<ProductResponseDTO>> getProductsByCategory(@PathVariable Long id) {
        return ResponseEntity.ok(categoryService.getProductsByCategory(id));
    }

    @PutMapping("/admin/categories/{id}")
    public ResponseEntity<String> updateCategory(@PathVariable Long id, @Valid @RequestBody CategoryRequestDTO categoryRequest) {
        categoryService.updateCategory(id, categoryRequest);
        return ResponseEntity.ok("Category updated successfully");
    }

    @DeleteMapping("/admin/categories/{id}")
    public ResponseEntity<String> deleteCategory(@PathVariable Long id) {
        categoryService.deleteCategory(id);
        return ResponseEntity.ok("Category deleted successfully");
    }

    // --- Brand APIs ---

    @PostMapping("/admin/brands")
    public ResponseEntity<String> addBrand(@Valid @RequestBody BrandRequestDTO brandRequest) {
        categoryService.addBrand(brandRequest);
        return ResponseEntity.ok("Brand added successfully");
    }

    @GetMapping("/brands")
    public ResponseEntity<List<Brand>> getAllBrands() {
        return ResponseEntity.ok(categoryService.getAllBrands());
    }

    @PutMapping("/admin/brands/{id}")
    public ResponseEntity<String> updateBrand(@PathVariable Long id, @Valid @RequestBody BrandRequestDTO brandRequest) {
        categoryService.updateBrand(id, brandRequest);
        return ResponseEntity.ok("Brand updated successfully");
    }

    @DeleteMapping("/admin/brands/{id}")
    public ResponseEntity<String> deleteBrand(@PathVariable Long id) {
        categoryService.deleteBrand(id);
        return ResponseEntity.ok("Brand deleted successfully");
    }
}
