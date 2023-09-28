package com.teamchallenge.marketplace.common.file.impl;

import com.cloudinary.Cloudinary;
import com.teamchallenge.marketplace.common.file.FileUpload;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Map;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class FileUploadImpl implements FileUpload {

    private final Cloudinary cloudinary;

    @Override
    public String uploadFile(MultipartFile file) throws IOException {
         return cloudinary
                 .uploader()
                 .upload(file.getBytes(), Map.of("public_id", UUID.randomUUID().toString()))
                 .get("url")
                 .toString();
    }
}
