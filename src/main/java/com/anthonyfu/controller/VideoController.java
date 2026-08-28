package com.anthonyfu.controller;

import com.anthonyfu.model.VideoInfo;
import com.anthonyfu.service.VideoService;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/video")
public class VideoController {

	private final VideoService videoService;

	public VideoController(VideoService videoService) {
		this.videoService = videoService;
	}

	/** 取得所有影片清單（標題、簡介、分類、播放連結等） */
	@GetMapping("/list")
	public Flux<VideoInfo> list() {
		return videoService.listVideos();
	}

	/** 取得單一影片的詳細資訊 */
	@GetMapping("/info/{filename}")
	public Mono<ResponseEntity<VideoInfo>> info(@PathVariable String filename) {
		return videoService.getVideo(filename).map(ResponseEntity::ok).defaultIfEmpty(ResponseEntity.notFound().build());
	}
}
