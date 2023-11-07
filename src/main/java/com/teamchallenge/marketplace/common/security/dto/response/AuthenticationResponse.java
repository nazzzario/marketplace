package com.teamchallenge.marketplace.common.security.dto.response;

import java.util.UUID;

public record AuthenticationResponse(UUID userReference,
                                     String authenticationToken) {
}
