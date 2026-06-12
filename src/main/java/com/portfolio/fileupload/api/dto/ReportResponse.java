package com.portfolio.fileupload.api.dto;

import java.time.LocalDate;

public record ReportResponse(
        LocalDate bizDate,
        long rowsCount
) {
}
