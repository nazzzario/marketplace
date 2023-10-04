package com.teamchallenge.marketplace.common.file;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface FileUpload {
    String uploadFile(MultipartFile multipartFile) throws IOException;

    List<String> uploadFiles(List<MultipartFile> multipartFiles);
}
