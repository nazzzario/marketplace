package com.teamchallenge.marketplace.common.exception;

import lombok.Getter;

@Getter
public class ClientBackendException extends RuntimeException {
    private final ErrorCode errorCode;

    public ClientBackendException(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

}
