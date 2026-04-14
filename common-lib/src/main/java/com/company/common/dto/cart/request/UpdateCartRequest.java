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
public class UpdateCartRequest {

    private Long userId;

    @NotNull(message = "ProductId is required")
    private Long productId;

    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;
}