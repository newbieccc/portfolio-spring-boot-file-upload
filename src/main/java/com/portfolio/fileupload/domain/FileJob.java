package com.portfolio.fileupload.domain;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "file_job")
public class FileJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String originalFileName;

    @Column(nullable = false)
    private String storedPath;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private FileJobStatus status;

    @Column(length = 500)
    private String errorMessage;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    private LocalDateTime processedAt;

    protected FileJob() {

    }

    public static FileJob uploaded(String originalFileName, String storedPath) {
        FileJob job = new FileJob();
        job.originalFileName = originalFileName;
        job.storedPath = storedPath;
        job.status = FileJobStatus.UPLOADED;
        job.createdAt = LocalDateTime.now();
        return job;
    }

    public void markProcessing() {
        this.status = FileJobStatus.PROCESSING;
        this.errorMessage = null;
    }

    public void markSuccess() {
        this.status = FileJobStatus.SUCCESS;
        this.processedAt = LocalDateTime.now();
    }

    public void markFailed(String msg) {
        this.status = FileJobStatus.FAILED;
        this.errorMessage = msg;
        this.processedAt = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public String getOriginalFileName() {
        return originalFileName;
    }

    public String getStoredPath() {
        return storedPath;
    }

    public FileJobStatus getStatus() {
        return status;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public LocalDateTime getProcessedAt() {
        return processedAt;
    }

}
