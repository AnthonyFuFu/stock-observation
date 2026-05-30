package com.anthonyfu.service;

import java.time.LocalDate;
import org.springframework.stereotype.Service;

import com.anthonyfu.model.entity.Stock;
import com.anthonyfu.model.entity.StockPrice;
import com.anthonyfu.model.entity.StockQuote;
import com.anthonyfu.repository.StockPriceRepository;
import com.anthonyfu.repository.StockQuoteRepository;
import com.anthonyfu.repository.StockRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StockService {

    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final StockQuoteRepository stockQuoteRepository;

    public Flux<Stock> getAllStocks() {
        return stockRepository.findAll();
    }
    
    public Mono<Stock> getStockBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol);
    }
    
    public Flux<StockPrice> getHistoricalPrices(String symbol, LocalDate startDate, LocalDate endDate) {
        return stockRepository.findBySymbol(symbol)
                .flatMapMany(stock -> stockPriceRepository.findByStockIdAndTradeDateBetweenOrderByTradeDate(
                        stock.getId(), startDate, endDate));
    }
    
    public Mono<StockQuote> getCurrentQuote(String symbol) {
        return stockQuoteRepository.findByStockSymbol(symbol);
    }
    
    public Flux<Stock> getTopGainers(int limit) {
        return stockRepository.findTopGainers(limit);
    }
    
    public Flux<Stock> getTopLosers(int limit) {
        return stockRepository.findTopLosers(limit);
    }
    
    public Mono<Stock> createStock(Stock stock) {
        return stockRepository.save(stock);
    }
    
    public Mono<StockQuote> updateQuote(StockQuote quote) {
        return stockQuoteRepository.save(quote);
    }
}
