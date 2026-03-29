package com.company.cart_service.dto.request;

import lombok.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AddToCartRequest {

    private Long userId;

    @NotNull(message = "ProductId is required")
    private Long productId;

    @Min(value = 1, message = "Quantity must be at least 1")
    private int quantity;
}