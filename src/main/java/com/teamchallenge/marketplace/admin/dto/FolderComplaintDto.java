package com.teamchallenge.marketplace.admin.dto;

import java.util.List;
import java.util.UUID;

public record FolderComplaintDto(
        UUID productReference,
       List<ComplaintDto> complaints) {
}