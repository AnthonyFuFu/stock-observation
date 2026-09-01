package com.anthonyfu.service;

import org.springframework.stereotype.Service;

import com.anthonyfu.model.entity.Stock;
import com.anthonyfu.model.entity.StockConceptCategory;
import com.anthonyfu.repository.StockConceptCategoryRepository;
import com.anthonyfu.repository.StockConceptRelationRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class StockConceptCategoryService
        extends AbstractReactiveCrudService<StockConceptCategory, Long, StockConceptCategoryRepository> {

    private final StockConceptCategoryRepository categoryRepository;
    private final StockConceptRelationRepository relationRepository;

    public StockConceptCategoryService(StockConceptCategoryRepository categoryRepository,
            StockConceptRelationRepository relationRepository) {
        super(categoryRepository);
        this.categoryRepository = categoryRepository;
        this.relationRepository = relationRepository;
    }

    @Override
    protected Long extractId(StockConceptCategory entity) {
        return entity.getId();
    }

    @Override
    protected void assignId(StockConceptCategory entity, Long id) {
        entity.setId(id);
    }

    public Flux<StockConceptCategory> getAllCategories() {
        return categoryRepository.findAllByOrderByCodeAsc();
    }

    public Mono<StockConceptCategory> getCategoryById(Long id) {
        return categoryRepository.findById(id);
    }

    public Mono<StockConceptCategory> getCategoryByCode(String code) {
        return categoryRepository.findByCode(code.toUpperCase());
    }

    public Flux<Stock> getStocksByCategory(String code) {
        return relationRepository.findStocksByCategoryCode(code.toUpperCase());
    }

    public Mono<StockConceptCategory> createCategory(StockConceptCategory category) {
        return categoryRepository.save(category);
    }

    public Mono<StockConceptCategory> updateCategory(Long id, StockConceptCategory category) {
        return updateById(id, category);
    }

    public Mono<Void> deleteCategory(Long id) {
        return categoryRepository.deleteById(id);
    }
}
