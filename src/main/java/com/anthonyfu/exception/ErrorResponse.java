package com.anthonyfu.exception;

import java.time.Instant;

/**
 * 統一的錯誤回應格式，讓所有 API 錯誤都有一致的欄位。
 */
public record ErrorResponse(
        Instant timestamp,
        int status,
        String error,
        String message,
        String path) {

    public static ErrorResponse of(int status, String error, String message, String path) {
        return new ErrorResponse(Instant.now(), status, error, message, path);
    }
}
