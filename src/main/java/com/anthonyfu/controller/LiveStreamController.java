package com.anthonyfu.controller;

import com.anthonyfu.service.LiveStreamService;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/live")
public class LiveStreamController {

    private final LiveStreamService liveStreamService;

    public LiveStreamController(LiveStreamService liveStreamService) {
        this.liveStreamService = liveStreamService;
    }

    /** 取得 HLS 播放清單 */
    @GetMapping("/{roomId}/index.m3u8")
    public Mono<Void> getPlaylist(
            @PathVariable String roomId,
            ServerHttpResponse response
    ) {
        return liveStreamService.proxyPlaylist(roomId, response);
    }

    /** 取得 HLS 片段（.ts / .m3u8 / .mp4）*/
    @GetMapping("/{roomId}/{filename}")
    public Mono<Void> getSegment(
            @PathVariable String roomId,
            @PathVariable String filename,
            ServerHttpResponse response
    ) {
        return liveStreamService.proxySegment(roomId, filename, response);
    }
}
