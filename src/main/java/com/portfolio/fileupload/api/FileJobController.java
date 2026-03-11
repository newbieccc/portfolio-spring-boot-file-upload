package com.portfolio.fileupload.api;

import com.portfolio.fileupload.api.dto.FileJobResponse;
import com.portfolio.fileupload.service.FileJobService;
import jakarta.validation.constraints.NotNull;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api")
public class FileJobController {

    private final FileJobService fileJobService;

    public FileJobController(FileJobService fileJobService) {
        this.fileJobService = fileJobService;
    }

    @PostMapping("/files")
    public FileJobResponse upload(@RequestPart("file") @NotNull MultipartFile file) {
        return FileJobResponse.from(fileJobService.upload(file));
    }

}
