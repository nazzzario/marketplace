package com.teamchallenge.marketplace.common.security.dto.request;

public record AuthenticationRequest(String email,
                                    String password) {
}
