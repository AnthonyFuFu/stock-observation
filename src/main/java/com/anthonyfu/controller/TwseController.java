package com.anthonyfu.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import com.anthonyfu.model.CandlePoint;
import com.anthonyfu.model.twse.CompanyInfo;
import com.anthonyfu.model.twse.MarketInfo;
import com.anthonyfu.model.twse.StockDailyInfo;
import com.anthonyfu.service.TwseService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/twse")
public class TwseController {
	
    private final TwseService twseService;
    
    // http://localhost:8080/api/twse/stock/2330
    @GetMapping("/stock/{code}")
    public Mono<StockDailyInfo> getStockInfo(@PathVariable String code) {
        return twseService.getStockDailyInfo(code);
    }
    // http://localhost:8080/api/twse/market
    @GetMapping("/market")
    public Mono<MarketInfo> getMarketInfo() {
        return twseService.getMarketInfo();
    }
    // http://localhost:8080/api/twse/company?query=台積電
    @GetMapping("/company")
    public Mono<CompanyInfo> searchCompany(@RequestParam String query) {
        return twseService.getCompanyInfo(query);
    }

    // http://localhost:8080/api/twse/candle/2330  → 個股當月日K棒
    @GetMapping("/candle/{code}")
    public Mono<List<CandlePoint>> getStockCandle(@PathVariable String code) {
        return twseService.getStockCandles(code);
    }

    // http://localhost:8080/api/twse/taiex/candle?date=20240101  → 加權指數當月K棒
    @GetMapping("/taiex/candle")
    public Mono<List<CandlePoint>> getTaiexCandle(@RequestParam(required = false) String date) {
        return twseService.getTaiexCandles(date);
    }
}