package com.portfolio.fileupload.api;

import com.portfolio.fileupload.api.dto.FileJobResponse;
import com.portfolio.fileupload.service.FileJobService;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class FileJobController {

    private final FileJobService fileJobService;

    public FileJobController(FileJobService fileJobService) {
        this.fileJobService = fileJobService;
    }

    @PostMapping(value = "/files", consumes = "multipart/form-data")
    public FileJobResponse upload(@RequestPart("file") @NotNull MultipartFile file) {
        return FileJobResponse.from(fileJobService.upload(file));
    }

    @PostMapping("/files/{id}/process")
    public FileJobResponse process(@PathVariable Long id) {
        return FileJobResponse.from(fileJobService.process(id));
    }

}
