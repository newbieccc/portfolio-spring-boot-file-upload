package com.portfolio.fileupload.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Service
public class FileStorageService {

    private final Path storageDir;

    public FileStorageService(@Value("${app.storage-dir}") String dir) {
        this.storageDir = Paths.get(dir).toAbsolutePath().normalize();
    }

    public String save(MultipartFile file) {
        try {
            Files.createDirectories(storageDir);

            String original = StringUtils.hasText(file.getOriginalFilename())
                    ? file.getOriginalFilename()
                    : "upload.csv";

            String savedName = UUID.randomUUID() + "_" + original;

            Path target = storageDir.resolve(savedName);

            Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

            return target.toString();

        } catch (IOException e) {
            throw new RuntimeException("파일 저장에 실패했습니다.");
        }
    }
}
