package com.anthonyfu.service;

import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 直播 HLS 串流服務
 *
 * 代理 MediaMTX 的 HLS 串流給前端瀏覽器。
 * MediaMTX 負責 RTMP → HLS 轉碼，本服務只負責轉發。
 *
 * 流程：
 * OBS → RTMP → MediaMTX(:1935) → HLS(:8888) → 本服務代理 → 瀏覽器
 */
@Service
public class LiveStreamService {

    private final WebClient webClient;

    /** MediaMTX 的 HLS 位址 */
    private final String mediaMtxHlsBase = "http://localhost:8888";

    public LiveStreamService(WebClient webClient) {
        this.webClient = webClient;
    }

    /**
     * 代理 HLS 播放清單（.m3u8）
     */
    public Mono<Void> proxyPlaylist(String roomId, ServerHttpResponse response) {
        String url = mediaMtxHlsBase + "/" + roomId + "/index.m3u8";
        return proxyMediaMtx(url, "application/vnd.apple.mpegurl", response);
    }

    /**
     * 代理 HLS 影片片段（.ts）或 HLS 子清單
     */
    public Mono<Void> proxySegment(String roomId, String filename, ServerHttpResponse response) {
        String url = mediaMtxHlsBase + "/" + roomId + "/" + filename;
        String contentType = filename.endsWith(".ts") ? "video/mp2t"
                : filename.endsWith(".m3u8") ? "application/vnd.apple.mpegurl"
                : filename.endsWith(".mp4") ? "video/mp4"
                : "application/octet-stream";
        return proxyMediaMtx(url, contentType, response);
    }

    /**
     * 通用代理：從 MediaMTX 取得資料，直接寫入 ServerHttpResponse
     */
    private Mono<Void> proxyMediaMtx(String url, String contentType, ServerHttpResponse response) {
        return webClient.get()
                .uri(url)
                .exchangeToMono(clientResponse -> {
                    int statusCode = clientResponse.statusCode().value();

                    // MediaMTX 回 404 = 還沒有人推流
                    if (statusCode == 404) {
                        response.setStatusCode(HttpStatus.NOT_FOUND);
                        return response.setComplete();
                    }

                    response.setStatusCode(HttpStatus.valueOf(statusCode));
                    response.getHeaders().set("Content-Type", contentType);
                    response.getHeaders().set("Access-Control-Allow-Origin", "*");
                    response.getHeaders().set("Cache-Control", "no-cache");

                    // 轉發 Content-Length
                    HttpHeaders remoteHeaders = clientResponse.headers().asHttpHeaders();
                    if (remoteHeaders.getContentLength() > 0) {
                        response.getHeaders().setContentLength(remoteHeaders.getContentLength());
                    }

                    Flux<DataBuffer> body = clientResponse.bodyToFlux(DataBuffer.class);
                    return response.writeWith(body);
                });
    }
}
