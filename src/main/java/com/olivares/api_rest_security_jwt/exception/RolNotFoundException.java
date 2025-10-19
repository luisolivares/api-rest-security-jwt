package com.olivares.api_rest_security_jwt.exception;

public class RolNotFoundException extends RuntimeException {
    public RolNotFoundException(String message) {
        super(message);
    }
}
