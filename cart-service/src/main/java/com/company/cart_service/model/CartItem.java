package com.company.cart_service.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Table("cart_item")
public class CartItem {

    @Id
    private Long id;

    private Long cartId;

    private Long productId;

    // Snapshot fields
    private String productName;
    private Double price;

    private Integer quantity;
}