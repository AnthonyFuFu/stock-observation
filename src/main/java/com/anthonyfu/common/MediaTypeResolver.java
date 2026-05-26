package com.anthonyfu.common;

import org.springframework.http.MediaType;

import java.util.Map;

/**
 * 影片 / 媒體檔案的 Content-Type 解析工具
 * 
 * 根據檔名或 URL 的副檔名自動判斷對應的 MediaType。
 * 可在影片串流、檔案下載等場景共用。
 */
public final class MediaTypeResolver {

    private static final Map<String, String> MEDIA_TYPES = Map.of(
            "mp4", "video/mp4",
            "webm", "video/webm",
            "ogg", "video/ogg",
            "avi", "video/x-msvideo",
            "mkv", "video/x-matroska",
            "mov", "video/quicktime",
            "flv", "video/x-flv",
            "wmv", "video/x-ms-wmv"
    );

    private static final String DEFAULT_TYPE = "video/mp4";

    private MediaTypeResolver() {
        // Utility class，禁止實例化
    }

    /**
     * 根據檔名或 URL 解析 MediaType
     *
     * @param filenameOrUrl 檔名或 URL（例如 "sample.mp4" 或 "https://example.com/video.webm"）
     * @return 對應的 MediaType，預設為 video/mp4
     */
    public static MediaType resolve(String filenameOrUrl) {
        String ext = extractExtension(filenameOrUrl);
        return MediaType.parseMediaType(MEDIA_TYPES.getOrDefault(ext, DEFAULT_TYPE));
    }

    private static String extractExtension(String filename) {
        // 移除 query string（例如 "video.mp4?token=abc" → "video.mp4"）
        int queryIndex = filename.indexOf('?');
        if (queryIndex > 0) {
            filename = filename.substring(0, queryIndex);
        }
        int dotIndex = filename.lastIndexOf('.');
        return (dotIndex > 0) ? filename.substring(dotIndex + 1).toLowerCase() : "";
    }
}
