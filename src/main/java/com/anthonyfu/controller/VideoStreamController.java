package com.anthonyfu.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.*;

import com.anthonyfu.service.VideoStreamService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/video")
public class VideoStreamController {

    private final VideoStreamService videoStreamService;

    public VideoStreamController(VideoStreamService videoStreamService) {
        this.videoStreamService = videoStreamService;
    }

    /** 串流本地影片（支援 Range / 拖曳進度條）*/
    @GetMapping("/local/{filename}")
    public Mono<ResponseEntity<byte[]>> streamLocal(
            @PathVariable String filename,
            @RequestHeader(value = "Range", required = false) String rangeHeader
    ) {
        return videoStreamService.streamLocal(filename, rangeHeader);
    }

    /** 串流遠端影片（代理轉發）*/
    @GetMapping("/remote")
    public Mono<Void> streamRemote(
            @RequestParam String url,
            @RequestHeader(value = "Range", required = false) String rangeHeader,
            ServerHttpResponse response
    ) {
        return videoStreamService.streamRemote(url, rangeHeader, response);
    }
}
