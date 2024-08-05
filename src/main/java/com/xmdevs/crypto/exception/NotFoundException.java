package com.xmdevs.crypto.exception;

public class NotFoundException extends RuntimeException {

    public NotFoundException(Domain message) {
        super(message + " not found");
    }
}
