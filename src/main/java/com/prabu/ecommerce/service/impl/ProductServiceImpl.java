package com.prabu.ecommerce.service.impl;

import com.prabu.ecommerce.dto.requestdto.ProductImageDTO;
import com.prabu.ecommerce.dto.requestdto.ProductRequestDTO;
import com.prabu.ecommerce.dto.responsedto.ProductResponseDTO;
import com.prabu.ecommerce.exception.ProductException;
import com.prabu.ecommerce.model.*;
import com.prabu.ecommerce.repository.BrandRepository;
import com.prabu.ecommerce.repository.CategoryRepository;
import com.prabu.ecommerce.repository.ProductRepository;
import com.prabu.ecommerce.service.FileStorageService;
import com.prabu.ecommerce.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final FileStorageService fileStorageService;
    private final BrandRepository brandRepository;

    @Override
    @Transactional
    public boolean addProduct(ProductRequestDTO request, List<MultipartFile> images) {
        if (request.getSku() != null && !request.getSku().isBlank()) {
            if (productRepository.existsBySku(request.getSku())) {
                throw new ProductException("SKU '" + request.getSku() + "' already exists");
            }
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ProductException("Category not found with id: " + request.getCategoryId()));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ProductException("Brand not found with id: " + request.getBrandId()));

        Product product = Product.builder()
                .name(request.getName())
                .description(request.getDescription())
                .price(request.getPrice())
                .stock(request.getStock())
                .sku(request.getSku())
                .status(ProductStatus.ACTIVE)
                .category(category)
                .brand(brand)
                .build();

        if (images != null && !images.isEmpty()) {
            saveImages(images, product);
        }
        productRepository.save(product);
        return true;
    }

    @Override
    public List<ProductResponseDTO> getAllActiveProducts() {
        return productRepository.findByStatus(ProductStatus.ACTIVE).stream()
                .map(this::convertToResponseDTO)
                .collect(Collectors.toList());
    }

    @Override
    public ProductResponseDTO getProductById(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found with id: " + id));
        return convertToResponseDTO(product);
    }

    @Override
    @Transactional
    public boolean updateProduct(Long id, ProductRequestDTO request, List<MultipartFile> images) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found with id: " + id));

        if (request.getSku() != null && !request.getSku().equalsIgnoreCase(product.getSku())) {
            if (productRepository.existsBySku(request.getSku())) {
                throw new ProductException("SKU '" + request.getSku() + "' already exists");
            }
        }

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ProductException("Category not found with id: " + request.getCategoryId()));

        Brand brand = brandRepository.findById(request.getBrandId())
                .orElseThrow(() -> new ProductException("Brand not found with id: " + request.getBrandId()));

        product.setName(request.getName());
        product.setDescription(request.getDescription());
        product.setPrice(request.getPrice());
        product.setStock(request.getStock());
        product.setSku(request.getSku());
        product.setCategory(category);
        product.setBrand(brand);

        if (images != null && !images.isEmpty()) {
            product.getImages().clear();
            saveImages(images, product);
        }

        productRepository.save(product);
        return true;
    }

    @Override
    @Transactional
    public boolean deleteProduct(Long id) {
        if (!productRepository.existsById(id)) {
            throw new ProductException("Product not found with id: " + id);
        }
        productRepository.deleteById(id);
        return true;
    }

    @Override
    @Transactional
    public boolean toggleProductStatus(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found with id: " + id));
        product.setStatus(product.getStatus() == ProductStatus.ACTIVE ? ProductStatus.INACTIVE : ProductStatus.ACTIVE);
        productRepository.save(product);
        return true;
    }

    private void saveImages(List<MultipartFile> images, Product product) {
        boolean firstImage = product.getImages().isEmpty();
        for (MultipartFile image : images) {
            String imageUrl = fileStorageService.saveFile(image);
            ProductImage productImage = ProductImage.builder()
                    .imageUrl(imageUrl)
                    .isPrimary(firstImage)
                    .product(product)
                    .build();
            product.getImages().add(productImage);
            firstImage = false;
        }
    }

    @Override
    public ProductResponseDTO convertToResponseDTO(Product product) {
        List<ProductImageDTO> imageDTOs = product.getImages().stream()
                .map(image -> ProductImageDTO.builder()
                        .id(image.getId())
                        .imageUrl(image.getImageUrl())
                        .isPrimary(image.getIsPrimary())
                        .build())
                .collect(Collectors.toList());

        return ProductResponseDTO.builder()
                .id(product.getId())
                .name(product.getName())
                .description(product.getDescription())
                .price(product.getPrice())
                .stock(product.getStock())
                .sku(product.getSku())
                .status(product.getStatus())
                .images(imageDTOs)
                .build();
    }

    @Override
    @Transactional
    public void uploadImages(Long id, List<MultipartFile> images) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new ProductException("Product not found with id: " + id));

        if (images != null && !images.isEmpty()) {
            saveImages(images, product);
            productRepository.save(product);
        }
    }

    @Override
    public void deleteImage(Long id, Long imgId) {
        if(!productRepository.existsById(id)) {
            throw new ProductException("Product not found with id: " + id);
        }

    }
}
