package com.teamchallenge.marketplace.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum ErrorCode {
    PRODUCT_NOT_FOUND("404-001",
            "Cannot find product",
            404),
    UNKNOWN_SERVER_ERROR("500-001",
            "Unknown server error",
            500);

    private final ErrorData errorData;

    ErrorCode(String code, String description, Integer httpResponseCode) {
        this.errorData = new ErrorData(code, description, httpResponseCode);
    }


    @AllArgsConstructor
    @Getter
    @Setter
    public static final class ErrorData {
        private String code;
        private String description;
        private Integer httpResponseCode;
    }
}
