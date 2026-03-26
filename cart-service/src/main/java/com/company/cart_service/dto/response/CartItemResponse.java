package com.company.cart_service.dto.response;

import lombok.*;

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