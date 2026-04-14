package com.company.common.dto.cart.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemResponse {

    private Long productId;
    private String productName;
    private Double price;
    private Integer quantity;

    private Double itemTotal; // price * quantity
}