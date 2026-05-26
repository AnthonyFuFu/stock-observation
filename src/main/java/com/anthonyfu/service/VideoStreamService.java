package com.anthonyfu.service;

import com.anthonyfu.common.MediaTypeResolver;
import com.anthonyfu.common.RangeRequest;

import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.io.InputStream;

@Service
public class VideoStreamService {

	private final ResourceLoader resourceLoader;
	private final WebClient webClient;

	private final String localPath = "classpath:videos/";

	public VideoStreamService(ResourceLoader resourceLoader, WebClient webClient) {
		this.resourceLoader = resourceLoader;
		this.webClient = webClient;
	}

	// ========================================
	// 本地影片串流
	// ========================================

	/**
	 * 串流本地影片，支援 Range 請求（瀏覽器拖曳進度條）
	 */
	public Mono<ResponseEntity<byte[]>> streamLocal(String filename, String rangeHeader) {
		return loadResource(filename).map(resource -> {
			long fileSize = getContentLength(resource);
			RangeRequest range = RangeRequest.of(rangeHeader, fileSize);
			byte[] data = readBytes(resource, range.start(), range.end());

			return ResponseEntity.status(range.status()).contentType(MediaTypeResolver.resolve(filename))
					.header("Accept-Ranges", "bytes").header("Content-Range", range.contentRangeHeader(data.length))
					.contentLength(data.length).body(data);
		});
	}

	// ========================================
	// 遠端影片串流
	// ========================================

	/**
	 * 串流代理遠端影片，直接寫入 ServerHttpResponse
	 */
	public Mono<Void> streamRemote(String videoUrl, String rangeHeader, ServerHttpResponse response) {
		return buildRemoteRequest(videoUrl, rangeHeader).exchangeToMono(clientResponse -> {
			HttpHeaders remoteHeaders = clientResponse.headers().asHttpHeaders();
			forwardHeaders(response, clientResponse.statusCode().value(), remoteHeaders, videoUrl);

			Flux<DataBuffer> body = clientResponse.bodyToFlux(DataBuffer.class);
			return response.writeWith(body);
		});
	}

	// ========================================
	// Private — 資源讀取
	// ========================================

	private Mono<Resource> loadResource(String filename) {
		return Mono.fromSupplier(() -> resourceLoader.getResource(localPath + filename)).filter(Resource::exists)
				.switchIfEmpty(Mono.error(new RuntimeException("影片不存在: " + filename)));
	}

	private long getContentLength(Resource resource) {
		try {
			return resource.contentLength();
		} catch (IOException e) {
			throw new RuntimeException("無法取得檔案大小", e);
		}
	}

	private byte[] readBytes(Resource resource, long start, long end) {
		try (InputStream is = resource.getInputStream()) {
			int length = (int) (end - start + 1);
			byte[] buffer = new byte[length];
			is.skip(start);
			int bytesRead = is.read(buffer, 0, length);
			if (bytesRead < length) {
				byte[] trimmed = new byte[Math.max(bytesRead, 0)];
				System.arraycopy(buffer, 0, trimmed, 0, trimmed.length);
				return trimmed;
			}
			return buffer;
		} catch (IOException e) {
			throw new RuntimeException("讀取影片失敗", e);
		}
	}

	// ========================================
	// Private — 遠端請求 & Header 轉發
	// ========================================

	private WebClient.RequestHeadersSpec<?> buildRemoteRequest(String videoUrl, String rangeHeader) {
		WebClient.RequestHeadersSpec<?> request = webClient.get().uri(videoUrl);
		if (rangeHeader != null) {
			request = webClient.get().uri(videoUrl).header("Range", rangeHeader);
		}
		return request;
	}

	private void forwardHeaders(ServerHttpResponse response, int statusCode, HttpHeaders remoteHeaders, String videoUrl) {
		// 狀態碼
		response.setStatusCode(HttpStatus.valueOf(statusCode));

		// Content-Type（遠端沒提供時用 URL 副檔名推斷）
		response.getHeaders().setContentType(remoteHeaders.getContentType() != null ? remoteHeaders.getContentType() : MediaTypeResolver.resolve(videoUrl));

		// Content-Length
		if (remoteHeaders.getContentLength() > 0) {
			response.getHeaders().setContentLength(remoteHeaders.getContentLength());
		}

		// Accept-Ranges
		response.getHeaders().set("Accept-Ranges", "bytes");

		// Content-Range
		String contentRange = remoteHeaders.getFirst("Content-Range");
		if (contentRange != null) {
			response.getHeaders().set("Content-Range", contentRange);
		}
	}
}
