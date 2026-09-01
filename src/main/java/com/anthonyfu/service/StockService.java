package com.anthonyfu.service;

import java.time.LocalDate;
import org.springframework.stereotype.Service;

import com.anthonyfu.model.entity.Stock;
import com.anthonyfu.model.entity.StockPrice;
import com.anthonyfu.model.entity.StockQuote;
import com.anthonyfu.repository.StockPriceRepository;
import com.anthonyfu.repository.StockQuoteRepository;
import com.anthonyfu.repository.StockRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StockService extends AbstractReactiveCrudService<Stock, Long, StockRepository> {

    private final StockRepository stockRepository;
    private final StockPriceRepository stockPriceRepository;
    private final StockQuoteRepository stockQuoteRepository;

    public StockService(StockRepository stockRepository, StockPriceRepository stockPriceRepository,
            StockQuoteRepository stockQuoteRepository) {
        super(stockRepository);
        this.stockRepository = stockRepository;
        this.stockPriceRepository = stockPriceRepository;
        this.stockQuoteRepository = stockQuoteRepository;
    }

    @Override
    protected Long extractId(Stock entity) {
        return entity.getId();
    }

    @Override
    protected void assignId(Stock entity, Long id) {
        entity.setId(id);
    }

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

    public Mono<Stock> updateStock(String symbol, Stock stock) {
        return updateByLookup(stockRepository.findBySymbol(symbol), stock);
    }

    public Mono<Void> deleteStock(String symbol) {
        return deleteByLookup(stockRepository.findBySymbol(symbol));
    }
}
