package com.anthonyfu.exception;

import java.util.NoSuchElementException;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ResponseStatusException;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

/**
 * 全域例外處理：統一 API 錯誤回應格式，取代各 service 內零散的 error handling。
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResponseStatusException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleResponseStatus(ResponseStatusException ex, ServerHttpRequest request) {
        HttpStatus status = HttpStatus.valueOf(ex.getStatusCode().value());
        return Mono.just(build(status, ex.getReason() != null ? ex.getReason() : status.getReasonPhrase(), request));
    }

    @ExceptionHandler({ IllegalArgumentException.class, WebExchangeBindException.class })
    public Mono<ResponseEntity<ErrorResponse>> handleBadRequest(Exception ex, ServerHttpRequest request) {
        log.warn("Bad request at {}: {}", request.getPath(), ex.getMessage());
        return Mono.just(build(HttpStatus.BAD_REQUEST, ex.getMessage(), request));
    }

    @ExceptionHandler({ NoSuchElementException.class })
    public Mono<ResponseEntity<ErrorResponse>> handleNotFound(Exception ex, ServerHttpRequest request) {
        return Mono.just(build(HttpStatus.NOT_FOUND, ex.getMessage(), request));
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleDataIntegrityViolation(DataIntegrityViolationException ex, ServerHttpRequest request) {
        log.warn("Data integrity violation at {}: {}", request.getPath(), ex.getMessage());
        return Mono.just(build(HttpStatus.CONFLICT, "資料衝突，請確認輸入內容", request));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleUnexpected(Exception ex, ServerHttpRequest request) {
        log.error("Unhandled exception at {}", request.getPath(), ex);
        return Mono.just(build(HttpStatus.INTERNAL_SERVER_ERROR, "系統發生非預期錯誤", request));
    }

    private ResponseEntity<ErrorResponse> build(HttpStatus status, String message, ServerHttpRequest request) {
        ErrorResponse body = ErrorResponse.of(status.value(), status.getReasonPhrase(), message, request.getPath().value());
        return ResponseEntity.status(status).body(body);
    }
}
