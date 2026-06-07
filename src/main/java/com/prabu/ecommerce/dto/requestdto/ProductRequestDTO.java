package com.prabu.ecommerce.dto.requestdto;

import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class ProductRequestDTO {
    @NotBlank(message = "Product name is required")
    @Size(min = 2, max = 255, message = "Name must be between 2 and 255 characters")
    private String name;

    private String description;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.1", message = "Price must be greater than 0")
    private double price;

    @NotNull(message = "Stock is required")
    @Min(value = 0, message = "Stock cannot be negative")
    private int stock;

    private String sku;

    @NotNull(message = "Category is required")
    private Long categoryId;

    @NotNull(message = "Brand is required")
    private Long brandId;

    private boolean featured;
}
