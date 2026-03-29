package com.company.cart_service.dto.request;

import lombok.*;
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