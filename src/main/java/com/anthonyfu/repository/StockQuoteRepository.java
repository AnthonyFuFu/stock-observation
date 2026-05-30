package com.anthonyfu.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.anthonyfu.model.entity.StockQuote;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockQuoteRepository extends ReactiveCrudRepository<StockQuote, Long> {
    
    Mono<StockQuote> findByStockId(Long stockId);
    
    @Query("SELECT q.* FROM stock_quotes q JOIN stocks s ON q.stock_id = s.id WHERE s.symbol = :symbol")
    Mono<StockQuote> findByStockSymbol(String symbol);
    
    @Query("SELECT q.* FROM stock_quotes q ORDER BY q.change_percent DESC LIMIT :limit")
    Flux<StockQuote> findTopMovers(int limit);
    
    @Query("SELECT q.* FROM stock_quotes q JOIN stocks s ON q.stock_id = s.id WHERE s.sector = :sector")
    Flux<StockQuote> findQuotesBySector(String sector);
}