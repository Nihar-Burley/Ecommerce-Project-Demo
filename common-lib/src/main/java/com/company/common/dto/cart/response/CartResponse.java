package com.company.common.dto.cart.response;

import com.company.common.dto.cart.response.CartItemResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

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