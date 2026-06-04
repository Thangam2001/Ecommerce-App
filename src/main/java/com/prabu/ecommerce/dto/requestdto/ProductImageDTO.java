package com.prabu.ecommerce.dto.requestdto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ProductImageDTO {
    private Long id;
    private String imageUrl;
    private boolean isPrimary;
}
