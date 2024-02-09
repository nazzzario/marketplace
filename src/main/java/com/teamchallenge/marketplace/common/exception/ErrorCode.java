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
    EMAIL_NOT_EXISTS("400-003",
            "Email does not exists",
            400),
    PASSWORD_RESET_TOKEN_NOT_EXISTS("400-004",
            "Password reset token not exists",
            400),
    NEW_PASSWORD_SAME_AS_OLD_PASSWORD("400-005",
            "New user password cannot be equal to old password",
            400),
    CANNOT_ADD_PRODUCT_TO_FAVORITE("400-006",
            "Cannot add product to favorites",
            400),
    LIMIT_IS_EXHAUSTED("400-007",
            "Cannot add entity to list or raise ad, because limit is exhausted",
            400),
    PASSWORD_NOT_EXISTS("400-008", "Password not exists",
            400),
    FORBIDDEN("403-001",
            "Forbidden",
            403),
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
            503),
    UNABLE_TO_SEND_EMAIL("503-001",
            "Unable send email",
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
