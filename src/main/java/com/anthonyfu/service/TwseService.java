package com.anthonyfu.service;

import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.anthonyfu.model.CandlePoint;
import com.anthonyfu.model.twse.CompanyInfo;
import com.anthonyfu.model.twse.MarketInfo;
import com.anthonyfu.model.twse.StockDailyInfo;

import reactor.core.publisher.Mono;

@Service
public class TwseService {

    private final WebClient webClient;
    
    public TwseService(WebClient webClient) {
        this.webClient = webClient.mutate()
                .baseUrl("https://www.twse.com.tw")
                .defaultHeader("User-Agent", "Mozilla/5.0")
                .build();
    }
    
    // 獲取個股日成交資訊
    public Mono<StockDailyInfo> getStockDailyInfo(String stockCode) {
        String formattedDate = LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/exchangeReport/STOCK_DAY")
                        .queryParam("response", "json")
                        .queryParam("date", formattedDate)
                        .queryParam("stockNo", stockCode)
                        .build())
                .retrieve()
                .bodyToMono(StockDailyInfo.class)
                .doOnError(error -> System.err.println("Error fetching TWSE data: " + error.getMessage()));
    }
    
    // 獲取大盤資訊
    public Mono<MarketInfo> getMarketInfo() {
        
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/exchangeReport/MI_INDEX")
                        .queryParam("response", "json")
                        .build())
                .retrieve()
                .bodyToMono(MarketInfo.class)
                .doOnError(error -> System.err.println("Error fetching market data: " + error.getMessage()));
    }
    
    // 獲取上市公司基本資料
    public Mono<CompanyInfo> getCompanyInfo(String query) {
        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/zh/api/codeQuery")
                        .queryParam("query", query)
                        .build())
                .retrieve()
                .bodyToMono(CompanyInfo.class)
                .doOnError(error -> System.err.println("Error fetching company info: " + error.getMessage()));
    }

    // 獲取個股日K棒資料（當月，ROC 民國年格式）
    public Mono<List<CandlePoint>> getStockCandles(String stockCode) {
        return getStockDailyInfo(stockCode)
                .map(info -> convertToCandles(info, false))
                .onErrorReturn(Collections.emptyList());
    }

    // 獲取台灣加權指數歷史K棒（西元年格式）
    public Mono<List<CandlePoint>> getTaiexCandles(String date) {
        String queryDate = (date != null && !date.isBlank())
                ? date
                : LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));

        return webClient.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/exchangeReport/TAIEX")
                        .queryParam("response", "json")
                        .queryParam("date", queryDate)
                        .queryParam("type", "ALL")
                        .build())
                .retrieve()
                .bodyToMono(StockDailyInfo.class)
                .map(info -> convertToCandles(info, true))
                .onErrorReturn(Collections.emptyList());
    }

    private List<CandlePoint> convertToCandles(StockDailyInfo info, boolean isIndex) {
        List<CandlePoint> result = new ArrayList<>();
        if (info == null || info.getData() == null || info.getFields() == null) return result;
        if (!"OK".equalsIgnoreCase(info.getStat())) return result;

        List<String> fields = info.getFields();
        int dateIdx  = indexOf(fields, "日期");
        int openIdx  = indexOf(fields, isIndex ? "開盤指數" : "開盤價");
        int highIdx  = indexOf(fields, isIndex ? "最高指數" : "最高價");
        int lowIdx   = indexOf(fields, isIndex ? "最低指數" : "最低價");
        int closeIdx = indexOf(fields, isIndex ? "收盤指數" : "收盤價");
        int volIdx   = indexOf(fields, "成交股數");

        if (dateIdx < 0 || closeIdx < 0) return result;

        for (List<String> row : info.getData()) {
            Long ts = parseDateEpoch(row.get(dateIdx));
            if (ts == null) continue;
            Double close = parseNum(col(row, closeIdx));
            if (close == null) continue;
            result.add(new CandlePoint(
                    ts,
                    parseNum(col(row, openIdx)),
                    parseNum(col(row, highIdx)),
                    parseNum(col(row, lowIdx)),
                    close,
                    parseNum(col(row, volIdx))));
        }
        return result;
    }

    private int indexOf(List<String> fields, String keyword) {
        for (int i = 0; i < fields.size(); i++) {
            if (fields.get(i).contains(keyword)) return i;
        }
        return -1;
    }

    private String col(List<String> row, int idx) {
        return (idx >= 0 && idx < row.size()) ? row.get(idx) : null;
    }

    private Double parseNum(String s) {
        if (s == null || s.isBlank() || s.equals("--") || s.equals("X")) return null;
        try {
            return Double.parseDouble(s.replaceAll("[,+%]", "").trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    // 支援民國年 113/01/02 及西元年 2024/01/02
    private Long parseDateEpoch(String dateStr) {
        if (dateStr == null || dateStr.isBlank()) return null;
        try {
            String[] parts = dateStr.trim().split("/");
            if (parts.length != 3) return null;
            int year = Integer.parseInt(parts[0].trim());
            if (year < 1900) year += 1911;
            int month = Integer.parseInt(parts[1].trim());
            int day   = Integer.parseInt(parts[2].trim());
            return LocalDate.of(year, month, day)
                    .atStartOfDay(ZoneId.of("Asia/Taipei"))
                    .toEpochSecond();
        } catch (Exception e) {
            return null;
        }
    }
}