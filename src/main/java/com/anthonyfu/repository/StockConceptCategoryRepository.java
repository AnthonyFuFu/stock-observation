package com.anthonyfu.repository;

import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.anthonyfu.model.entity.StockConceptCategory;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface StockConceptCategoryRepository extends ReactiveCrudRepository<StockConceptCategory, Long> {

    Mono<StockConceptCategory> findByCode(String code);

    Flux<StockConceptCategory> findAllByOrderByCodeAsc();
}
