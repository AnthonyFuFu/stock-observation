package com.anthonyfu.service;

import com.anthonyfu.model.VideoInfo;
import com.anthonyfu.model.entity.Video;
import com.anthonyfu.repository.VideoRepository;

import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.NoSuchElementException;

@Service
public class VideoUploadService {

	private final VideoRepository videoRepository;
	private final VideoStorageService videoStorageService;
	private final VideoService videoService;

	public VideoUploadService(VideoRepository videoRepository, VideoStorageService videoStorageService,
			VideoService videoService) {
		this.videoRepository = videoRepository;
		this.videoStorageService = videoStorageService;
		this.videoService = videoService;
	}

	/** 後台上傳：驗證檔案 → 寫入實體檔案 → 新增一筆 videos 資料列（檔案與資料同時建立） */
	public Mono<VideoInfo> upload(FilePart file, String title, String description, String category, String tags, String releaseDate) {
		String safeName = videoStorageService.sanitizeFilename(file.filename());
		if (!videoStorageService.isAllowedExtension(safeName)) {
			return Mono.error(new IllegalArgumentException("不支援的影片格式: " + safeName));
		}

		String storedFilename = System.currentTimeMillis() + "_" + safeName;

		return videoStorageService.save(file, storedFilename)
				.then(Mono.defer(() -> videoRepository
						.save(Video.builder().filename(storedFilename).title(blankToDefault(title, safeName))
								.description(description).category(blankToDefault(category, "未分類"))
								.tags(parseTags(tags)).releaseDate(parseDate(releaseDate)).build())))
				.flatMap(saved -> videoService.getVideo(saved.getFilename()));
	}

	/** 刪除影片（同時移除實體檔案與資料庫紀錄） */
	public Mono<Void> delete(String filename) {
		return videoRepository.findByFilename(filename)
				.switchIfEmpty(Mono.error(new NoSuchElementException("找不到影片: " + filename)))
				.flatMap(video -> videoStorageService.delete(filename).then(videoRepository.delete(video)));
	}

	private String blankToDefault(String value, String fallback) {
		return (value == null || value.isBlank()) ? fallback : value;
	}

	private String[] parseTags(String tags) {
		if (tags == null || tags.isBlank()) {
			return new String[0];
		}
		return Arrays.stream(tags.split(",")).map(String::trim).filter(t -> !t.isEmpty()).toArray(String[]::new);
	}

	private LocalDate parseDate(String date) {
		if (date == null || date.isBlank()) {
			return null;
		}
		try {
			return LocalDate.parse(date);
		} catch (Exception e) {
			return null;
		}
	}
}
