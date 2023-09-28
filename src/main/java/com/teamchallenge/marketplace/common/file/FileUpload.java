package com.teamchallenge.marketplace.common.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;
import java.util.UUID;

public interface FileUpload {
    String uploadFile(MultipartFile multipartFile, UUID productReference) throws IOException;

    List<String> uploadFiles(List<MultipartFile> multipartFiles, UUID productReference);
}
