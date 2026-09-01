package com.anthonyfu.service;

import java.util.List;

import com.anthonyfu.model.CandlePoint;

import reactor.core.publisher.Mono;

/**
 * K 線資料提供者的策略介面，讓不同資料來源（Finnhub、TWSE...）
 * 以一致的方式提供 K 線資料，方便未來擴充新的資料來源。
 *
 * 註：Finnhub/TWSE 的報價、公司資訊等其他 API 回傳格式差異過大，
 * 前端也直接依賴各自的 DTO 形狀，因此本介面刻意只聚焦在「取得 K 線」，
 * 不強行統一其餘方法，避免產生沒有實質效益的抽象。
 */
public interface StockCandleProvider {

    /** 資料來源代號，例如 "finnhub"、"twse" */
    String providerKey();

    /** 取得指定股票代號的 K 線資料 */
    Mono<List<CandlePoint>> getCandles(String symbol);
}
