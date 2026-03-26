package com.company.cart_service.exception;


public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException() {
        super("Insufficient stock available");
    }

    public InsufficientStockException(String message) {
        super(message);
    }
}