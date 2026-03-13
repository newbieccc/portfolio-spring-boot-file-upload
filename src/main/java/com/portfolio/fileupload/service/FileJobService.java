package com.portfolio.fileupload.service;

import com.portfolio.fileupload.domain.FileJob;
import com.portfolio.fileupload.domain.ProcessedRow;
import com.portfolio.fileupload.repo.FileJobRepository;
import com.portfolio.fileupload.repo.ProcessedRowRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public class FileJobService {

    private static final Logger log = LoggerFactory.getLogger(FileJobService.class);

    private final FileStorageService storageService;
    private final FileJobRepository fileJobRepository;
    private final ProcessedRowRepository processedRowRepository;

    public FileJobService(FileStorageService storageService, FileJobRepository fileJobRepository, ProcessedRowRepository processedRowRepository) {
        this.storageService = storageService;
        this.fileJobRepository = fileJobRepository;
        this.processedRowRepository = processedRowRepository;
    }

    @Transactional
    public FileJob upload(MultipartFile file) {
        String storedPath = storageService.save(file);

        String originalName = file.getOriginalFilename() != null
                ? file.getOriginalFilename()
                : "upload.csv";

        FileJob job = FileJob.uploaded(originalName, storedPath);
        return fileJobRepository.save(job);
    }

    @Transactional
    public FileJob process(Long id) {
        FileJob job = fileJobRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("파일 작업을 찾을 수 없습니다. id=" + id));

        log.info("process 시작 fileJobId={} currentStatus={}", job.getId(), job.getStatus());

        job.markProcessing();

        try {

            processedRowRepository.deleteByFileJobId(job.getId());

            List<CsvProcessor.CsvRow> rows = CsvProcessor.parseAndValidate(job.getStoredPath());

            for (CsvProcessor.CsvRow row : rows) {
                ProcessedRow entity = ProcessedRow.of(
                        job.getId(),
                        row.bizDate(),
                        row.itemCode(),
                        row.qty()
                );
                processedRowRepository.save(entity);
            }

            job.markSuccess();
            log.info("process 성공 fileJobId={} savedRows={}", job.getId(), rows.size());

            return job;
        } catch (Exception e) {
            job.markFailed(e.getMessage());
            log.warn("process 실패 fileJobId={} reason={}", job.getId(), e.getMessage());

            return job;
        }
    }
}
