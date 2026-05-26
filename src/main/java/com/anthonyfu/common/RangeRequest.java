package com.anthonyfu.common;

import org.springframework.http.HttpStatus;

/**
 * HTTP Range 請求的 Value Object
 * 
 * 封裝 Range header 的解析邏輯，提供計算 Content-Range、狀態碼等方法。 可在任何需要處理 Range
 * 請求的場景共用（影片串流、檔案下載等）。
 */
public record RangeRequest(long start, long end, long fileSize, boolean isRangeRequest) {

	/** 預設每次回傳的最大區塊大小：1MB */
	private static final long DEFAULT_CHUNK_SIZE = 1024 * 1024;

	/**
	 * 解析 Range header，產生 RangeRequest
	 *
	 * @param rangeHeader HTTP Range header（例如 "bytes=0-1048575"），可為 null
	 * @param fileSize    檔案總大小
	 * @return RangeRequest 實例
	 */
	public static RangeRequest of(String rangeHeader, long fileSize) {
		if (rangeHeader == null || !rangeHeader.startsWith("bytes=")) {
			return new RangeRequest(0, fileSize - 1, fileSize, false);
		}

		String[] ranges = rangeHeader.substring(6).split("-");
		long start = Long.parseLong(ranges[0]);
		long end = (ranges.length > 1 && !ranges[1].isEmpty()) ? Long.parseLong(ranges[1])
				: Math.min(start + DEFAULT_CHUNK_SIZE - 1, fileSize - 1);

		return new RangeRequest(start, end, fileSize, true);
	}

	/** 請求的內容長度 */
	public long contentLength() {
		return end - start + 1;
	}

	/** 根據是否為 Range 請求決定 HTTP 狀態碼 */
	public HttpStatus status() {
		return isRangeRequest ? HttpStatus.PARTIAL_CONTENT : HttpStatus.OK;
	}

	/**
	 * 產生 Content-Range header 值
	 *
	 * @param actualDataLength 實際讀取到的資料長度（可能小於請求範圍，例如檔案尾端）
	 * @return Content-Range 值，例如 "bytes 0-1048575/5242880"
	 */
	public String contentRangeHeader(long actualDataLength) {
		return String.format("bytes %d-%d/%d", start, start + actualDataLength - 1, fileSize);
	}
}