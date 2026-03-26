package com.company.cart_service.dto.request;

import lombok.*;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UpdateCartRequest {

    @NotBlank(message = "UserId is required")
    private String userId;

    @NotBlank(message = "ProductId is required")
    private Long productId;

    @Min(value = 0, message = "Quantity cannot be negative")
    private int quantity;
}