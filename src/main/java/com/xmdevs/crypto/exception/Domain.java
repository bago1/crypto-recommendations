package com.xmdevs.crypto.exception;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.ToString;

@ToString
@AllArgsConstructor
public enum Domain {
    CRYPTO_NOT_FOUND("Crypto not found");

    private final String message;

}

