package com.company.user_service.exception;

public class CustomException extends RuntimeException {

    private final String errorCode;
    private final int status;

    public CustomException(String message, String errorCode, int status) {
        super(message);
        this.errorCode = errorCode;
        this.status = status;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public int getStatus() {
        return status;
    }
}
