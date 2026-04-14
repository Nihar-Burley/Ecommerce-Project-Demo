package com.company.cart_service.mapper;


import com.company.cart_service.model.Cart;
import com.company.cart_service.model.CartItem;
import com.company.common.dto.cart.response.CartItemResponse;
import com.company.common.dto.cart.response.CartResponse;

import java.util.List;


public class CartMapper {

    public static CartItemResponse toCartItemResponse(CartItem item) {
        return CartItemResponse.builder()
                .productId(item.getProductId())
                .productName(item.getProductName())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .itemTotal(item.getPrice() * item.getQuantity())
                .build();
    }

    public static CartResponse toCartResponse(Cart cart, List<CartItemResponse> items) {

        double total = items.stream()
                .mapToDouble(CartItemResponse::getItemTotal)
                .sum();

        return CartResponse.builder()
                .userId(cart.getUserId())
                .items(items)
                .totalAmount(total)
                .build();
    }
}