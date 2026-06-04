package com.prabu.ecommerce.controller;

import com.prabu.ecommerce.dto.requestdto.ProductRequestDTO;
import com.prabu.ecommerce.dto.responsedto.ProductResponseDTO;
import com.prabu.ecommerce.service.ProductService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api")
public class ProductController {

    @Autowired
    private ProductService productService;

    // --- Public APIs ---

    @GetMapping("/products")
    public ResponseEntity<List<ProductResponseDTO>> getAllProducts() {
        return ResponseEntity.ok(productService.getAllActiveProducts());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ProductResponseDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }

    // --- Admin APIs ---

    @PostMapping(value = "/admin/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> addProduct(@RequestPart("product") @Valid ProductRequestDTO request,
                                            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        productService.addProduct(request, images);
        return ResponseEntity.ok("Product successfully added.");
    }

    @PutMapping(value = "/admin/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> updateProduct(@PathVariable Long id,
                                               @RequestPart("product") @Valid ProductRequestDTO request,
                                               @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        productService.updateProduct(id, request, images);
        return ResponseEntity.ok("Product successfully updated.");
    }

    @DeleteMapping("/admin/products/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok("Product successfully deleted.");
    }

    @PatchMapping("/admin/products/{id}/status")
    public ResponseEntity<String> toggleProductStatus(@PathVariable Long id) {
        productService.toggleProductStatus(id);
        return ResponseEntity.ok("Product status toggled successfully.");
    }
}
