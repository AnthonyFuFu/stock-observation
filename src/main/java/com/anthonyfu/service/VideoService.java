package com.anthonyfu.service;

import com.anthonyfu.model.VideoInfo;
import com.anthonyfu.model.entity.Video;
import com.anthonyfu.repository.VideoRepository;

import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;

@Service
public class VideoService {

	private final VideoRepository videoRepository;
	private final VideoStorageService videoStorageService;

	public VideoService(VideoRepository videoRepository, VideoStorageService videoStorageService) {
		this.videoRepository = videoRepository;
		this.videoStorageService = videoStorageService;
	}

	/** 取得所有影片清單（資料來自 DB videos 表，可自由新增/編輯資料列來擴充） */
	public Flux<VideoInfo> listVideos() {
		return videoRepository.findAllByOrderByCategoryAscTitleAsc().flatMap(this::toVideoInfo);
	}

	/** 依檔名取得單一影片詳細資訊 */
	public Mono<VideoInfo> getVideo(String filename) {
		return videoRepository.findByFilename(filename).flatMap(this::toVideoInfo);
	}

	private Mono<VideoInfo> toVideoInfo(Video video) {
		return resolveSize(video.getFilename()).map(size -> {
			VideoInfo info = new VideoInfo();
			info.setFilename(video.getFilename());
			info.setTitle(video.getTitle());
			info.setDescription(video.getDescription());
			info.setCategory(video.getCategory());
			info.setTags(video.getTags() != null ? Arrays.asList(video.getTags()) : List.of());
			info.setReleaseDate(video.getReleaseDate() != null ? video.getReleaseDate().toString() : null);
			info.setVideoUrl("/api/video/local/" + video.getFilename());
			info.setSizeBytes(size);
			return info;
		});
	}

	/** 查詢實際影片檔案大小，檔案不存在時回傳 0（不影響資料庫資料的顯示）*/
	private Mono<Long> resolveSize(String filename) {
		return videoStorageService.resolve(filename)
				.map(videoStorageService::sizeOf)
				.defaultIfEmpty(0L);
	}
}

