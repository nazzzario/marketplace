package com.teamchallenge.marketplace.common.exception.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;

@Builder
@Schema(example = "{\n" +
        "  \"time\": \"current time\",\n" +
        "  \"errorCode\": \"ClientBackendException ErrorCode\",\n" +
        "  \"title\": \"ErrorCode name\",\n" +
        "  \"message\": \"error massage\",\n" +
        "  \"httpResponseCode\": 404,\n" +
        "  \"path\": \"URI path\"\n" +
        "}")
public record ExceptionResponseDto(String time,
                                   String errorCode,
                                   String title,
                                   String message,
                                   Integer httpResponseCode,
                                   String path) {
}
