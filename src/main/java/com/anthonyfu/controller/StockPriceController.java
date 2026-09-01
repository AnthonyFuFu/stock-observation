package com.anthonyfu.controller;

import java.time.LocalDate;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.anthonyfu.model.entity.StockPrice;
import com.anthonyfu.service.StockPriceService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/stocks/{symbol}/prices")
public class StockPriceController {

    private final StockPriceService stockPriceService;

    // GET /api/stocks/2330/prices
    @GetMapping
    public Flux<StockPrice> getPrices(@PathVariable String symbol) {
        return stockPriceService.getPricesBySymbol(symbol);
    }

    // GET /api/stocks/2330/prices/range?from=2024-01-01&to=2024-12-31
    @GetMapping("/range")
    public Flux<StockPrice> getPricesByDateRange(
            @PathVariable String symbol,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return stockPriceService.getPricesBySymbolAndDateRange(symbol, from, to);
    }

    // GET /api/stocks/2330/prices/latest?limit=30
    @GetMapping("/latest")
    public Flux<StockPrice> getLatestPrices(
            @PathVariable String symbol,
            @RequestParam(defaultValue = "30") int limit) {
        return stockPriceService.getLatestPrices(symbol, limit);
    }

    // POST /api/stocks/2330/prices
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<StockPrice> createPrice(@PathVariable String symbol, @RequestBody StockPrice price) {
        return stockPriceService.createPrice(price);
    }

    // PUT /api/stocks/2330/prices/{id}
    @PutMapping("/{id}")
    public Mono<StockPrice> updatePrice(
            @PathVariable String symbol,
            @PathVariable Long id,
            @RequestBody StockPrice price) {
        return stockPriceService.updatePrice(id, price);
    }

    // DELETE /api/stocks/2330/prices/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deletePrice(@PathVariable String symbol, @PathVariable Long id) {
        return stockPriceService.deletePrice(id);
    }
}
