package com.company.product.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("product")
public class Product {
    @Id
    private Long id;

    private String name;

    private String description;

    private Double price;

    private Integer stock;

    //For Audit Purpose
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
