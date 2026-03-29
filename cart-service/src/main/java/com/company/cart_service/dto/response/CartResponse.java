package com.company.cart_service.dto.response;

import lombok.*;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartResponse {

    private Long userId;

    private List<CartItemResponse> items;

    private Double totalAmount;
}