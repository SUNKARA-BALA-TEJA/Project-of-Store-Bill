package com.freshcart.service;

/**
 * Authentication failure with a stable machine-readable code so the API can
 * distinguish invalid credentials from pending, rejected or disabled accounts.
 */
public class AuthException extends RuntimeException {
    private final String code;

    public AuthException(String code, String message) {
        super(message);
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
