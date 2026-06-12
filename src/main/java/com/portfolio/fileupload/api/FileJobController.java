package com.portfolio.fileupload.api;

import com.portfolio.fileupload.api.dto.FileJobResponse;
import com.portfolio.fileupload.api.dto.ReportResponse;
import com.portfolio.fileupload.repo.ProcessedRowRepository;
import com.portfolio.fileupload.service.FileJobService;
import jakarta.validation.constraints.NotNull;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api")
public class FileJobController {

    private final FileJobService fileJobService;
    private final ProcessedRowRepository processedRowRepository;

    public FileJobController(FileJobService fileJobService, ProcessedRowRepository processedRowRepository) {
        this.fileJobService = fileJobService;
        this.processedRowRepository = processedRowRepository;
    }

    @PostMapping(value = "/files", consumes = "multipart/form-data")
    public FileJobResponse upload(@RequestPart("file") @NotNull MultipartFile file) {
        return FileJobResponse.from(fileJobService.upload(file));
    }

    @PostMapping("/files/{id}/process")
    public FileJobResponse process(@PathVariable Long id) {
        return FileJobResponse.from(fileJobService.process(id));
    }

    @GetMapping("/files/{id}")
    public FileJobResponse get(@PathVariable Long id) {
        return FileJobResponse.from(fileJobService.get(id));
    }

    @GetMapping("/reports")
    public List<ReportResponse> report(
            @RequestParam("date")
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date
    ) {
        long count = processedRowRepository.findByBizDate(date).size();

        return List.of(new ReportResponse(date, count));
    }
}
