package com.company.common.dto.cart.request;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartItemRequest {

    @NotNull(message = "ProductId is required")
    private Long productId;

    @Min(value = 1, message = "Quantity must be greater than 0")
    private int quantity;
}