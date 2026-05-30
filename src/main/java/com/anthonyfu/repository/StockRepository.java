package com.anthonyfu.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.anthonyfu.model.entity.Stock;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockRepository extends ReactiveCrudRepository<Stock, Long> {
    
    Mono<Stock> findBySymbol(String symbol);
    
    Flux<Stock> findByExchange(String exchange);
    
    @Query("SELECT s.* FROM stocks s WHERE s.sector = :sector ORDER BY s.symbol")
    Flux<Stock> findStocksBySector(String sector);
    
    @Query("SELECT s.* FROM stocks s JOIN stock_quotes q ON s.id = q.stock_id ORDER BY q.change_percent DESC LIMIT :limit")
    Flux<Stock> findTopGainers(int limit);
    
    @Query("SELECT s.* FROM stocks s JOIN stock_quotes q ON s.id = q.stock_id ORDER BY q.change_percent ASC LIMIT :limit")
    Flux<Stock> findTopLosers(int limit);
}