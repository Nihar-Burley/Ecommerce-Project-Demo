package com.company.cart_service.exception;

public class CartNotFoundException extends RuntimeException {

    public CartNotFoundException() {
        super("Cart not found");
    }

    public CartNotFoundException(String message) {
        super(message);
    }
}