package com.anthonyfu.controller;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anthonyfu.model.CandlePoint;
import com.anthonyfu.model.finnhub.FinnhubProfile;
import com.anthonyfu.model.finnhub.FinnhubQuote;
import com.anthonyfu.service.FinnhubService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/finnhub")
@RequiredArgsConstructor
public class FinnhubController {

    private final FinnhubService finnhubService;

    // GET /api/finnhub/quote/AAPL
    @GetMapping("/quote/{symbol}")
    public Mono<FinnhubQuote> getQuote(@PathVariable String symbol) {
        return finnhubService.getQuote(symbol.toUpperCase());
    }

    // GET /api/finnhub/candle/AAPL?resolution=D&days=90
    @GetMapping("/candle/{symbol}")
    public Mono<List<CandlePoint>> getCandles(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "D") String resolution,
            @RequestParam(defaultValue = "90") int days) {
        return finnhubService.getCandles(symbol.toUpperCase(), resolution, days);
    }

    // GET /api/finnhub/profile/AAPL  → 公司基本資料（名稱、交易所、產業）
    @GetMapping("/profile/{symbol}")
    public Mono<FinnhubProfile> getProfile(@PathVariable String symbol) {
        return finnhubService.getProfile(symbol.toUpperCase());
    }

    // GET /api/finnhub/market/us  → SPY, QQQ, DIA 即時報價
    @GetMapping("/market/us")
    public Mono<Map<String, FinnhubQuote>> getUsMarket() {
        return finnhubService.getUsMarket();
    }
}
