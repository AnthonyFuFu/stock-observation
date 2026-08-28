package com.anthonyfu.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

/**
 * 統一管理影片實體檔案的存放位置：
 * - 內建範例影片放在 classpath:videos/（隨專案打包，唯讀）
 * - 後台上傳的影片存放在外部資料夾 app.video.upload-dir（可寫入，不受 jar 打包限制）
 */
@Service
public class VideoStorageService {

	public static final Set<String> ALLOWED_EXTENSIONS = Set.of("mp4", "webm", "ogg", "mov", "mkv", "avi", "flv", "wmv");

	private static final String CLASSPATH_LOCATION = "classpath:videos/";

	private final ResourcePatternResolver resourcePatternResolver;
	private final Path uploadDir;

	public VideoStorageService(
			ResourcePatternResolver resourcePatternResolver,
			@Value("${app.video.upload-dir:uploads/videos}") String uploadDir
	) {
		this.resourcePatternResolver = resourcePatternResolver;
		this.uploadDir = Paths.get(uploadDir).toAbsolutePath().normalize();
	}

	/** 依檔名尋找影片資源：優先找外部上傳資料夾，找不到再找內建 classpath 影片；都找不到則回傳空 */
	public Mono<Resource> resolve(String filename) {
		return Mono.fromCallable(() -> {
			Path uploaded = resolveSafely(filename);
			if (Files.exists(uploaded)) {
				return (Resource) new FileSystemResource(uploaded);
			}
			Resource classpathResource = resourcePatternResolver.getResource(CLASSPATH_LOCATION + filename);
			return classpathResource.exists() ? classpathResource : null;
		}).subscribeOn(Schedulers.boundedElastic());
	}

	/** 將上傳的檔案寫入外部上傳資料夾 */
	public Mono<Void> save(FilePart file, String targetFilename) {
		return Mono.fromCallable(() -> {
			Files.createDirectories(uploadDir);
			return resolveSafely(targetFilename);
		}).subscribeOn(Schedulers.boundedElastic()).flatMap(file::transferTo);
	}

	/** 刪除外部上傳資料夾中的檔案（內建 classpath 範例影片不可刪除）*/
	public Mono<Void> delete(String filename) {
		return Mono.fromRunnable(() -> {
			try {
				Files.deleteIfExists(resolveSafely(filename));
			} catch (IOException e) {
				throw new RuntimeException("刪除影片檔案失敗: " + filename, e);
			}
		}).subscribeOn(Schedulers.boundedElastic()).then();
	}

	/** 去除路徑分隔符與特殊字元，避免路徑穿越或非法檔名 */
	public String sanitizeFilename(String rawName) {
		String base = Paths.get(rawName == null ? "" : rawName).getFileName().toString();
		return base.replaceAll("[^A-Za-z0-9._-]", "_");
	}

	public boolean isAllowedExtension(String filename) {
		int dot = filename.lastIndexOf('.');
		return dot > 0 && ALLOWED_EXTENSIONS.contains(filename.substring(dot + 1).toLowerCase());
	}

	public long sizeOf(Resource resource) {
		try {
			return resource.contentLength();
		} catch (IOException e) {
			return 0L;
		}
	}

	/** 確保解析後的路徑仍位於 uploadDir 底下，避免 "../" 之類的路徑穿越攻擊 */
	private Path resolveSafely(String filename) {
		Path resolved = uploadDir.resolve(filename).normalize();
		if (!resolved.startsWith(uploadDir)) {
			throw new SecurityException("非法的檔名: " + filename);
		}
		return resolved;
	}
}
