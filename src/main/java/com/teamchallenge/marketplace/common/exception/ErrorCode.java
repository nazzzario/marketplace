package com.teamchallenge.marketplace.common.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
public enum ErrorCode {
    INVALID_SEARCH_INPUT("400-001",
            "Invalid search parameter",
            400),
    EMAIL_ALREADY_EXISTS("400-002",
            "Email already exists",
            400),
    PRODUCT_NOT_FOUND("404-001",
            "Cannot find product",
            404),
    USER_NOT_FOUND("404-002",
            "Cannot find user",
            404),
    UNKNOWN_SERVER_ERROR("500-001",
            "Unknown server error",
            500),
    UNABLE_TO_SAVE_FILE("503-001",
            "Unable to save file",
            503);
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
