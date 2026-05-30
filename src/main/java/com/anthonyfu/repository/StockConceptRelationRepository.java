package com.anthonyfu.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.anthonyfu.model.entity.Stock;
import com.anthonyfu.model.entity.StockConceptCategory;
import com.anthonyfu.model.entity.StockConceptRelation;

import reactor.core.publisher.Flux;

public interface StockConceptRelationRepository extends ReactiveCrudRepository<StockConceptRelation, Long> {

    Flux<StockConceptRelation> findByStockId(Long stockId);

    Flux<StockConceptRelation> findByCategoryId(Long categoryId);

    // 查某分類代碼下的所有股票
    @Query("""
        SELECT s.* FROM stocks s
        JOIN stock_concept_relations r ON s.id = r.stock_id
        JOIN stock_concept_categories c ON c.id = r.category_id
        WHERE c.code = :categoryCode
        ORDER BY r.weight DESC, s.symbol
        """)
    Flux<Stock> findStocksByCategoryCode(String categoryCode);

    // 查某支股票隸屬的所有概念分類
    @Query("""
        SELECT c.* FROM stock_concept_categories c
        JOIN stock_concept_relations r ON c.id = r.category_id
        WHERE r.stock_id = :stockId
        ORDER BY c.code
        """)
    Flux<StockConceptCategory> findCategoriesByStockId(Long stockId);
}
