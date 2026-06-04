package com.prabu.ecommerce.controller;

import com.prabu.ecommerce.dto.requestdto.ProductRequestDTO;
import com.prabu.ecommerce.dto.responsedto.ApiResponse;
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
    public ResponseEntity<ApiResponse<List<ProductResponseDTO>>> getAllProducts() {
        List<ProductResponseDTO> products = productService.getAllActiveProducts();
        return ResponseEntity.ok(ApiResponse.success("Products fetched successfully", products));
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<ApiResponse<ProductResponseDTO>> getProductById(@PathVariable Long id) {
        ProductResponseDTO product = productService.getProductById(id);
        return ResponseEntity.ok(ApiResponse.success("Product fetched successfully", product));
    }

    // --- Admin APIs ---

    @PostMapping(value = "/admin/products", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> addProduct(@RequestPart("product") @Valid ProductRequestDTO request,
                                                         @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        productService.addProduct(request, images);
        return ResponseEntity.ok(ApiResponse.success("Product successfully added."));
    }

    @PutMapping(value = "/admin/products/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ApiResponse<String>> updateProduct(@PathVariable Long id,
                                                            @RequestPart("product") @Valid ProductRequestDTO request,
                                                            @RequestPart(value = "images", required = false) List<MultipartFile> images) {
        productService.updateProduct(id, request, images);
        return ResponseEntity.ok(ApiResponse.success("Product successfully updated."));
    }

    @DeleteMapping("/admin/products/{id}")
    public ResponseEntity<ApiResponse<String>> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.ok(ApiResponse.success("Product successfully deleted."));
    }

    @PatchMapping("/admin/products/{id}/status")
    public ResponseEntity<ApiResponse<String>> toggleProductStatus(@PathVariable Long id) {
        productService.toggleProductStatus(id);
        return ResponseEntity.ok(ApiResponse.success("Product status toggled successfully."));
    }

    @PostMapping("/admin/products/{id}/images")
    public ResponseEntity<ApiResponse<String>> uploadProductImages(@PathVariable Long id, @RequestPart("images") List<MultipartFile> images) {
        productService.uploadImages(id, images);
        return ResponseEntity.ok(ApiResponse.success("Product images successfully uploaded."));
    }

    @DeleteMapping("/admin/products/{id}/images/{imgId}")
    public ResponseEntity<ApiResponse<String>> deleteProductImages(@PathVariable Long id, @PathVariable Long imgId) {
        productService.deleteProductImage(id, imgId);
        return ResponseEntity.ok(ApiResponse.success("Product images successfully deleted."));
    }
}
