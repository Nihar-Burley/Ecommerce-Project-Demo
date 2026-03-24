package com.company.product.dto.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ProductResponse {

    private Long id;

    private String name;

    private String description;

    private Double price;

    private Integer stock;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}