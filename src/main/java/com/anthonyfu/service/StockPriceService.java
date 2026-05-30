package com.anthonyfu.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.anthonyfu.model.entity.StockPrice;
import com.anthonyfu.repository.StockPriceRepository;
import com.anthonyfu.repository.StockRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StockPriceService {

    private final StockPriceRepository stockPriceRepository;
    private final StockRepository stockRepository;

    public Flux<StockPrice> getPricesBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol)
                .flatMapMany(stock -> stockPriceRepository.findByStockIdOrderByTradeDateDesc(stock.getId()));
    }

    public Flux<StockPrice> getPricesBySymbolAndDateRange(String symbol, LocalDate from, LocalDate to) {
        return stockRepository.findBySymbol(symbol)
                .flatMapMany(stock -> stockPriceRepository
                        .findByStockIdAndTradeDateBetweenOrderByTradeDate(stock.getId(), from, to));
    }

    public Flux<StockPrice> getLatestPrices(String symbol, int limit) {
        return stockRepository.findBySymbol(symbol)
                .flatMapMany(stock -> stockPriceRepository.findLatestPrices(stock.getId(), limit));
    }

    public Mono<StockPrice> createPrice(StockPrice price) {
        return stockPriceRepository.save(price);
    }

    public Mono<StockPrice> updatePrice(Long id, StockPrice price) {
        return stockPriceRepository.findById(id)
                .flatMap(existing -> {
                    price.setId(id);
                    return stockPriceRepository.save(price);
                });
    }

    public Mono<Void> deletePrice(Long id) {
        return stockPriceRepository.deleteById(id);
    }
}
