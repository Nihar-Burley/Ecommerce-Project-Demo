package com.company.product.dto.request;

import lombok.Data;

import javax.validation.constraints.*;

    @Data
    public class ProductRequest {

        @NotBlank(message = "Product name is required")
        @Size(max = 255, message = "Name cannot exceed 255 characters")
        private String name;

        @Size(max = 500, message = "Description cannot exceed 500 characters")
        private String description;

        @NotNull(message = "Price is required")
        @Positive(message = "Price must be greater than 0")
        private Double price;

        @NotNull(message = "Stock is required")
        @Min(value = 0, message = "Stock cannot be negative")
        private Integer stock;
    }

