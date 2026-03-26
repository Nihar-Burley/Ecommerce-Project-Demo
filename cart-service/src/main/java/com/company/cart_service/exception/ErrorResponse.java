package com.company.cart_service.exception;

import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ErrorResponse {

    private String message;
    private String errorCode;
    private int status;
    private LocalDateTime timestamp;
}
