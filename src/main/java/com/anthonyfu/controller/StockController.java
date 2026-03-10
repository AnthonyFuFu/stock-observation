package com.anthonyfu.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anthonyfu.model.Stock;
import com.anthonyfu.model.StockPrice;
import com.anthonyfu.model.StockQuote;
import com.anthonyfu.service.StockService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/stocks")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;
    
    //http://localhost:8080/api/stocks
    @GetMapping
    public Flux<Stock> getAllStocks() {
        return stockService.getAllStocks();
    }
    // http://localhost:8080/api/stocks/AAPL
    @GetMapping("/{symbol}")
    public Mono<Stock> getStockBySymbol(@PathVariable String symbol) {
        return stockService.getStockBySymbol(symbol);
    }
    // http://localhost:8080/api/stocks/AAPL/quote
    @GetMapping("/{symbol}/quote")
    public Mono<StockQuote> getStockQuote(@PathVariable String symbol) {
        return stockService.getCurrentQuote(symbol);
    }
    // http://localhost:8080/api/stocks/AAPL/history?from=2023-01-01&to=2023-01-31
    @GetMapping("/{symbol}/history")
    public Flux<StockPrice> getStockPriceHistory(
            @PathVariable String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return stockService.getHistoricalPrices(symbol, from, to);
    }
    // http://localhost:8080/api/stocks/top-gainers?limit=5
    @GetMapping("/top-gainers")
    public Flux<Stock> getTopGainers(@RequestParam(defaultValue = "10") int limit) {
        return stockService.getTopGainers(limit);
    }
    // http://localhost:8080/api/stocks/top-losers?limit=5
    @GetMapping("/top-losers")
    public Flux<Stock> getTopLosers(@RequestParam(defaultValue = "10") int limit) {
        return stockService.getTopLosers(limit);
    }
    
    @PostMapping
    public Mono<Stock> createStock(@RequestBody Stock stock) {
        return stockService.createStock(stock);
    }
}