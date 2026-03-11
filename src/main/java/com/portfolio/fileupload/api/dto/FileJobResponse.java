package com.portfolio.fileupload.api.dto;

import com.portfolio.fileupload.domain.FileJob;
import com.portfolio.fileupload.domain.FileJobStatus;

import java.time.LocalDateTime;

public record FileJobResponse(
        Long id,
        String originalFileName,
        String storedPath,
        FileJobStatus status,
        String errorMessage,
        LocalDateTime createdAt,
        LocalDateTime processedAt
) {
    public static FileJobResponse from(FileJob job) {
        return new FileJobResponse(
                job.getId(),
                job.getOriginalFileName(),
                job.getStoredPath(),
                job.getStatus(),
                job.getErrorMessage(),
                job.getCreatedAt(),
                job.getProcessedAt()
        );
    }
}
