package com.anthonyfu.service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.anthonyfu.model.CompanyInfo;
import com.anthonyfu.model.MarketInfo;
import com.anthonyfu.model.StockDailyInfo;

import reactor.core.publisher.Mono;

@Service
public class TwseService {

    private final WebClient webClient;
    
    public TwseService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder
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
}