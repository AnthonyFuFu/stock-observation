package com.anthonyfu.controller;

import com.anthonyfu.model.VideoInfo;
import com.anthonyfu.service.VideoUploadService;

import lombok.RequiredArgsConstructor;

import org.springframework.http.MediaType;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/video/admin")
public class VideoAdminController {

	private final VideoUploadService videoUploadService;
	
	/** 後台影片管理：上傳影片檔案並同時寫入 videos 資料表、刪除影片 */
	@PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public Mono<VideoInfo> upload(
			@RequestPart("file") FilePart file,
			@RequestPart(value = "title", required = false) String title,
			@RequestPart(value = "description", required = false) String description,
			@RequestPart(value = "category", required = false) String category,
			@RequestPart(value = "tags", required = false) String tags,
			@RequestPart(value = "releaseDate", required = false) String releaseDate) {
		return videoUploadService.upload(file, title, description, category, tags, releaseDate);
	}

	@DeleteMapping("/{filename}")
	public Mono<Void> delete(@PathVariable String filename) {
		return videoUploadService.delete(filename);
	}
}
