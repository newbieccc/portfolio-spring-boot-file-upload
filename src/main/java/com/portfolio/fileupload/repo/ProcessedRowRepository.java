package com.portfolio.fileupload.repo;

import com.portfolio.fileupload.domain.ProcessedRow;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ProcessedRowRepository extends JpaRepository<ProcessedRow, Long> {

    void deleteByFileJobId(Long fileJobId);

    List<ProcessedRow> findByBizDate(LocalDate bizDate);

}
