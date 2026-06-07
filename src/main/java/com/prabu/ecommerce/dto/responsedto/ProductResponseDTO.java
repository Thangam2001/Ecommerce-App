package com.prabu.ecommerce.dto.responsedto;

import com.prabu.ecommerce.dto.requestdto.ProductImageDTO;
import com.prabu.ecommerce.model.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductResponseDTO {
    private Long id;
    private String name;
    private String description;
    private double price;
    private int stock;
    private String sku;
    private ProductStatus status;
    private boolean featured;
    private List<ProductImageDTO> images;
}
