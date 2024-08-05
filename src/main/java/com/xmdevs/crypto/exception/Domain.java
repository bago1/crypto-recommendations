package com.xmdevs.crypto.exception;

public enum Domain {
    CRYPTO_NOT_FOUND("Crypto not found");

    private final String message;

    Domain(String message) {
        this.message = message;
    }

    @Override
    public String toString() {
        return message;
    }
}

