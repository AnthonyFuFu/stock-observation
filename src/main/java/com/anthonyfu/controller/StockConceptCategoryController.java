package com.anthonyfu.controller;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.anthonyfu.model.entity.Stock;
import com.anthonyfu.model.entity.StockConceptCategory;
import com.anthonyfu.service.StockConceptCategoryService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/concept-categories")
@RequiredArgsConstructor
public class StockConceptCategoryController {

    private final StockConceptCategoryService categoryService;

    // GET /api/concept-categories
    @GetMapping
    public Flux<StockConceptCategory> getAllCategories() {
        return categoryService.getAllCategories();
    }

    // GET /api/concept-categories/{id}
    @GetMapping("/{id}")
    public Mono<StockConceptCategory> getCategoryById(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }

    // GET /api/concept-categories/code/AI
    @GetMapping("/code/{code}")
    public Mono<StockConceptCategory> getCategoryByCode(@PathVariable String code) {
        return categoryService.getCategoryByCode(code);
    }

    // GET /api/concept-categories/AI/stocks
    @GetMapping("/{code}/stocks")
    public Flux<Stock> getStocksByCategory(@PathVariable String code) {
        return categoryService.getStocksByCategory(code);
    }

    // POST /api/concept-categories
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<StockConceptCategory> createCategory(@RequestBody StockConceptCategory category) {
        return categoryService.createCategory(category);
    }

    // PUT /api/concept-categories/{id}
    @PutMapping("/{id}")
    public Mono<StockConceptCategory> updateCategory(
            @PathVariable Long id,
            @RequestBody StockConceptCategory category) {
        return categoryService.updateCategory(id, category);
    }

    // DELETE /api/concept-categories/{id}
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteCategory(@PathVariable Long id) {
        return categoryService.deleteCategory(id);
    }
}
