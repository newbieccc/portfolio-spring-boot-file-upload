package com.portfolio.fileupload.support;

import java.util.Map;

public record ApiErrorResponse(
        String code,
        String message,
        String traceId,
        Map<String, Object> details
) {
    public static ApiErrorResponse of(ErrorCode code, String message, String traceId, Map<String, Object> details) {
        return new ApiErrorResponse(code.name(), message, traceId, details);
    }
}
