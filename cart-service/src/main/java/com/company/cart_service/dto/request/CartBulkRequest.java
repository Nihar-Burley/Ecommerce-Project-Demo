package com.company.cart_service.dto.request;

import lombok.*;
import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CartBulkRequest {

    private Long userId;

    @NotEmpty(message = "Items list cannot be empty")
    @Valid
    private List<CartItemRequest> items;
}
