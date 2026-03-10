package com.portfolio.fileupload.repo;

import com.portfolio.fileupload.domain.FileJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileJobRepository extends JpaRepository<FileJob, Long> {
}
