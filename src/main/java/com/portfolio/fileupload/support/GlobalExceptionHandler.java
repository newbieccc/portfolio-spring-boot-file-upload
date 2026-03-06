package com.portfolio.fileupload.support;

import org.slf4j.MDC;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

import static com.portfolio.fileupload.support.TraceIdFilter.TRACE_ID;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiErrorResponse> handleValidation(MethodArgumentNotValidException e) {

        Map<String, Object> details = new HashMap<>();
        details.put("fieldErrors", e.getBindingResult().getFieldErrors().stream()
                .map(fe -> Map.of(
                        "field", fe.getField(),
                        "message", fe.getDefaultMessage()
                ))
                .toList());

        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.of(
                        ErrorCode.INVALID_REQUEST,
                        "요청 값이 올바르지 않습니다.",
                        MDC.get(TRACE_ID),
                        details
                ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiErrorResponse> handleIllegal(IllegalArgumentException e) {
        return ResponseEntity.badRequest()
                .body(ApiErrorResponse.of(
                        ErrorCode.CSV_INVALID,
                        e.getMessage(),
                        MDC.get(TRACE_ID),
                        Map.of()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiErrorResponse> handleAny(Exception e) {
        return ResponseEntity.internalServerError()
                .body(ApiErrorResponse.of(
                        ErrorCode.INTERNAL_ERROR,
                        "서버 오류가 발생했습니다.",
                        MDC.get(TRACE_ID),
                        Map.of()
                ));
    }
}
