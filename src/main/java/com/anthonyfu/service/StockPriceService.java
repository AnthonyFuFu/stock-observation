package com.anthonyfu.service;

import java.time.LocalDate;

import org.springframework.stereotype.Service;

import com.anthonyfu.model.entity.StockPrice;
import com.anthonyfu.repository.StockPriceRepository;
import com.anthonyfu.repository.StockRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StockPriceService extends AbstractReactiveCrudService<StockPrice, Long, StockPriceRepository> {

    private final StockPriceRepository stockPriceRepository;
    private final StockRepository stockRepository;

    public StockPriceService(StockPriceRepository stockPriceRepository, StockRepository stockRepository) {
        super(stockPriceRepository);
        this.stockPriceRepository = stockPriceRepository;
        this.stockRepository = stockRepository;
    }

    @Override
    protected Long extractId(StockPrice entity) {
        return entity.getId();
    }

    @Override
    protected void assignId(StockPrice entity, Long id) {
        entity.setId(id);
    }

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
        return updateById(id, price);
    }

    public Mono<Void> deletePrice(Long id) {
        return stockPriceRepository.deleteById(id);
    }
}
