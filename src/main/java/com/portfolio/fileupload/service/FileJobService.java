package com.portfolio.fileupload.service;

import com.portfolio.fileupload.domain.FileJob;
import com.portfolio.fileupload.repo.FileJobRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
public class FileJobService {

    private final FileStorageService storageService;
    private final FileJobRepository fileJobRepository;

    public FileJobService(FileStorageService storageService, FileJobRepository fileJobRepository) {
        this.storageService = storageService;
        this.fileJobRepository = fileJobRepository;
    }

    @Transactional
    public FileJob upload(MultipartFile file) {
        String storedPath = storageService.save(file);

        String originalName = file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.csv";
        FileJob job = FileJob.uploaded(originalName, storedPath);

        return fileJobRepository.save(job);
    }
}
