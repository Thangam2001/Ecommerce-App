package com.prabu.ecommerce.service.impl;

import com.prabu.ecommerce.dto.requestdto.BrandRequestDTO;
import com.prabu.ecommerce.dto.requestdto.CategoryRequestDTO;
import com.prabu.ecommerce.dto.requestdto.ProductImageDTO;
import com.prabu.ecommerce.dto.responsedto.ProductResponseDTO;
import com.prabu.ecommerce.exception.ProductException;
import com.prabu.ecommerce.model.Brand;
import com.prabu.ecommerce.model.Category;
import com.prabu.ecommerce.model.Product;
import com.prabu.ecommerce.model.ProductStatus;
import com.prabu.ecommerce.repository.BrandRepository;
import com.prabu.ecommerce.repository.CategoryRepository;
import com.prabu.ecommerce.repository.ProductRepository;
import com.prabu.ecommerce.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class CategoryServiceImpl implements CategoryService {
    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BrandRepository brandRepository;

    @Override
    @Transactional
    public boolean addCategory(CategoryRequestDTO categoryRequest) {
        if(categoryRepository.findByName(categoryRequest.getName()).isPresent()){
            throw new ProductException("Category already exist");
        }
        Category category = Category.builder()
                .name(categoryRequest.getName())
                .description(categoryRequest.getDescription())
                .build();
        categoryRepository.save(category);
        return true;
    }

    @Override
    @Transactional
    public boolean updateCategory(Long id, CategoryRequestDTO categoryRequest) {
        if(categoryRepository.findById(id).isEmpty()){
            throw new ProductException("Category does not exist");
        }
        categoryRepository.update(id, categoryRequest.getName(), categoryRequest.getDescription());
        return true;
    }

    @Override
    @Transactional
    public boolean deleteCategory(Long id) {
        if(categoryRepository.findById(id).isEmpty()){
            throw new ProductException("Category does not exist");
        }

        if(productRepository.existsByCategoryId(id)){
            throw new ProductException("Cannot delete category as it has associated products. Please delete or reassign products first.");
        }

        categoryRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Category> getAllCategories() {
        return  categoryRepository.findAll();
    }

    @Override
    public List<ProductResponseDTO> getProductsByCategory(Long categoryId) {
        if (!categoryRepository.existsById(categoryId)) {
            throw new ProductException("Category not found with id: " + categoryId);
        }

        List<Product> products = productRepository.findByCategoryIdAndStatus(categoryId, ProductStatus.ACTIVE);

        return products.stream().map(this::convertToResponseDTO).collect(Collectors.toList());
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
    public boolean addBrand(BrandRequestDTO brandRequest) {
        if(brandRepository.findByName(brandRequest.getName()).isPresent()){
            throw new ProductException("Brand already exist");
        }
        Brand brand = Brand.builder()
                .name(brandRequest.getName())
                .description(brandRequest.getDescription())
                .build();
        brandRepository.save(brand);
        return true;
    }

    @Override
    @Transactional
    public boolean updateBrand(Long id, BrandRequestDTO brandRequest) {
        if(brandRepository.findById(id).isEmpty()){
            throw new ProductException("Brand does not exist");
        }
        brandRepository.update(id, brandRequest.getName(), brandRequest.getDescription());
        return true;
    }

    @Override
    @Transactional
    public boolean deleteBrand(Long id) {
        if(brandRepository.findById(id).isEmpty()){
            throw new ProductException("Brand does not exist");
        }

        if(productRepository.existsByBrandId(id)){
            throw new ProductException("Cannot delete brand as it has associated products. Please delete or reassign products first.");
        }

        brandRepository.deleteById(id);
        return true;
    }

    @Override
    public List<Brand> getAllBrands() {
        return  brandRepository.findAll();
    }
}
