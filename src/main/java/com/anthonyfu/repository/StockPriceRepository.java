package com.anthonyfu.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.anthonyfu.model.entity.StockPrice;

import reactor.core.publisher.Flux;

import java.time.LocalDate;

public interface StockPriceRepository extends ReactiveCrudRepository<StockPrice, Long> {
    
    Flux<StockPrice> findByStockIdOrderByTradeDateDesc(Long stockId);
    
    Flux<StockPrice> findByStockIdAndTradeDateBetweenOrderByTradeDate(
            Long stockId, LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT * FROM stock_prices WHERE stock_id = :stockId ORDER BY trade_date DESC LIMIT :limit")
    Flux<StockPrice> findLatestPrices(Long stockId, int limit);
}
