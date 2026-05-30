package com.anthonyfu.service;

import org.springframework.stereotype.Service;

import com.anthonyfu.model.entity.StockConceptCategory;
import com.anthonyfu.model.entity.StockConceptRelation;
import com.anthonyfu.repository.StockConceptRelationRepository;
import com.anthonyfu.repository.StockRepository;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
@RequiredArgsConstructor
public class StockConceptRelationService {

    private final StockConceptRelationRepository relationRepository;
    private final StockRepository stockRepository;

    public Flux<StockConceptRelation> getRelationsByStockId(Long stockId) {
        return relationRepository.findByStockId(stockId);
    }

    public Flux<StockConceptRelation> getRelationsByCategoryId(Long categoryId) {
        return relationRepository.findByCategoryId(categoryId);
    }

    public Flux<StockConceptCategory> getCategoriesBySymbol(String symbol) {
        return stockRepository.findBySymbol(symbol.toUpperCase())
                .flatMapMany(stock -> relationRepository.findCategoriesByStockId(stock.getId()));
    }

    public Mono<StockConceptRelation> createRelation(StockConceptRelation relation) {
        return relationRepository.save(relation);
    }

    public Mono<StockConceptRelation> updateRelation(Long id, StockConceptRelation relation) {
        return relationRepository.findById(id)
                .flatMap(existing -> {
                    relation.setId(id);
                    return relationRepository.save(relation);
                });
    }

    public Mono<Void> deleteRelation(Long id) {
        return relationRepository.deleteById(id);
    }
}
