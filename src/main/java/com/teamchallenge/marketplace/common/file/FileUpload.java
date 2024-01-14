package com.teamchallenge.marketplace.common.file;

import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

public interface FileUpload {
    String uploadFile(MultipartFile multipartFile, UUID reference);

    void deleteFile(UUID reference);
}
