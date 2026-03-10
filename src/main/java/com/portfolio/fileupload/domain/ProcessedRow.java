package com.portfolio.fileupload.domain;

import jakarta.persistence.*;

import java.time.LocalDate;

@Entity
@Table(name = "processed_row",
        indexes = {
                @Index(name = "idx_processed_row_file_job_id", columnList = "fileJobId"),
                @Index(name = "idx_processed_row_bix_date", columnList = "bizDate")
        })
public class ProcessedRow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long fileJobId;

    @Column(nullable = false)
    private LocalDate bizDate;

    @Column(nullable = false)
    private String itemCode;

    @Column(nullable = false)
    private int qty;

    protected ProcessedRow() {
    }

    public static ProcessedRow of(Long fileJobId, LocalDate bizDate, String itemCode, int qty) {
        ProcessedRow row = new ProcessedRow();
        row.fileJobId = fileJobId;
        row.bizDate = bizDate;
        row.itemCode = itemCode;
        row.qty = qty;
        return row;
    }

    public Long getId() {
        return id;
    }

    public Long getFileJobId() {
        return fileJobId;
    }

    public LocalDate getBizDate() {
        return bizDate;
    }

    public String getItemCode() {
        return itemCode;
    }

    public int getQty() {
        return qty;
    }

}
