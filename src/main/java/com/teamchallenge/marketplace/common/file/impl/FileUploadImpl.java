package com.teamchallenge.marketplace.common.file.impl;

import com.cloudinary.Cloudinary;
import com.teamchallenge.marketplace.common.exception.ClientBackendException;
import com.teamchallenge.marketplace.common.exception.ErrorCode;
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
    public String uploadFile(MultipartFile file, UUID reference) {
        try {
            return cloudinary
                   .uploader()
                   .upload(file.getBytes(), Map.of("public_id", reference.toString()))
                   .get("url")
                   .toString();
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.UNABLE_TO_SAVE_FILE);
        }
    }

    @Override
    public void deleteFile(UUID reference) {
        try {
            cloudinary.uploader().destroy(reference.toString(), null);
        } catch (IOException e) {
            throw new ClientBackendException(ErrorCode.PRODUCT_NOT_FOUND);
        }

    }
}
