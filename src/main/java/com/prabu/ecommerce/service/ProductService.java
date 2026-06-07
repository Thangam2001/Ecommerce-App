package com.prabu.ecommerce.service;

import com.prabu.ecommerce.dto.requestdto.ProductRequestDTO;
import com.prabu.ecommerce.dto.responsedto.ProductResponseDTO;
import com.prabu.ecommerce.model.Product;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductService {
    boolean addProduct(ProductRequestDTO productRequestDTO, List<MultipartFile> images);
    
    List<ProductResponseDTO> getAllActiveProducts();
    
    ProductResponseDTO getProductById(Long id);
    
    boolean updateProduct(Long id, ProductRequestDTO request, List<MultipartFile> images);
    
    boolean deleteProduct(Long id);
    
    boolean toggleProductStatus(Long id);

    ProductResponseDTO convertToResponseDTO(Product product);

    void uploadImages(Long id, List<MultipartFile> images);

    void deleteProductImage(Long id, Long imgId);

    List<ProductResponseDTO> getFeaturedProducts();
}
