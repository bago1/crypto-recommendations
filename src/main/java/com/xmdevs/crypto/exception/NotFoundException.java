package com.xmdevs.crypto.exception;

import lombok.Getter;

@Getter
public class NotFoundException extends RuntimeException {
    private final Domain domain;

    public NotFoundException(Domain domain) {
        super(domain.toString());
        this.domain = domain;
    }

}
