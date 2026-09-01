package com.anthonyfu.service;

import org.springframework.stereotype.Service;

import com.anthonyfu.model.entity.StockQuote;
import com.anthonyfu.repository.StockQuoteRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StockQuoteService extends AbstractReactiveCrudService<StockQuote, Long, StockQuoteRepository> {

    private final StockQuoteRepository stockQuoteRepository;

    public StockQuoteService(StockQuoteRepository stockQuoteRepository) {
        super(stockQuoteRepository);
        this.stockQuoteRepository = stockQuoteRepository;
    }

    @Override
    protected Long extractId(StockQuote entity) {
        return entity.getId();
    }

    @Override
    protected void assignId(StockQuote entity, Long id) {
        entity.setId(id);
    }

    public Mono<StockQuote> getQuoteBySymbol(String symbol) {
        return stockQuoteRepository.findByStockSymbol(symbol);
    }

    public Mono<StockQuote> getQuoteByStockId(Long stockId) {
        return stockQuoteRepository.findByStockId(stockId);
    }

    public Flux<StockQuote> getTopMovers(int limit) {
        return stockQuoteRepository.findTopMovers(limit);
    }

    public Flux<StockQuote> getQuotesBySector(String sector) {
        return stockQuoteRepository.findQuotesBySector(sector);
    }

    public Mono<StockQuote> createQuote(StockQuote quote) {
        return stockQuoteRepository.save(quote);
    }

    public Mono<StockQuote> updateQuote(Long id, StockQuote quote) {
        return updateById(id, quote);
    }

    public Mono<Void> deleteQuote(Long id) {
        return stockQuoteRepository.deleteById(id);
    }
}
