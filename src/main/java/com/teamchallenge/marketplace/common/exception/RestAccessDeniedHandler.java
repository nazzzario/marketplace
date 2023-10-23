package com.teamchallenge.marketplace.common.exception;

import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.http.entity.ContentType;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedCaseInsensitiveMap;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Map;

@Component
public class RestAccessDeniedHandler implements AccessDeniedHandler {
    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException, ServletException {
        response.setStatus(HttpStatus.FORBIDDEN.value());
        response.setContentType(ContentType.APPLICATION_JSON.toString());

        Map<String, Object> mapResponse = new LinkedCaseInsensitiveMap<>();
        mapResponse.put("time", LocalDateTime.now());
        mapResponse.put("code", 403);
        mapResponse.put("description", "Forbidden");
        mapResponse.put("httpResponseCode", 403);
        mapResponse.put("path", request.getRequestURI());


        new ObjectMapper().writeValueAsString(mapResponse);

    }
}
