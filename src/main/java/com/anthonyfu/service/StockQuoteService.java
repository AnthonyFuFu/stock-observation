package com.anthonyfu.service;

import org.springframework.stereotype.Service;

import com.anthonyfu.model.entity.StockQuote;
import com.anthonyfu.repository.StockQuoteRepository;
import com.anthonyfu.repository.StockRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StockQuoteService {

    private final StockQuoteRepository stockQuoteRepository;
    private final StockRepository stockRepository;

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
        return stockQuoteRepository.findById(id)
                .flatMap(existing -> {
                    quote.setId(id);
                    return stockQuoteRepository.save(quote);
                });
    }

    public Mono<Void> deleteQuote(Long id) {
        return stockQuoteRepository.deleteById(id);
    }
}
