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

import com.anthonyfu.model.entity.StockConceptCategory;
import com.anthonyfu.model.entity.StockConceptRelation;
import com.anthonyfu.service.StockConceptRelationService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@RequestMapping("/concept-relations")
public class StockConceptRelationController {

	private final StockConceptRelationService relationService;

	// GET /api/concept-relations/stock/{stockId}
	@GetMapping("/stock/{stockId}")
	public Flux<StockConceptRelation> getRelationsByStockId(@PathVariable Long stockId) {
		return relationService.getRelationsByStockId(stockId);
	}

	// GET /api/concept-relations/category/{categoryId}
	@GetMapping("/category/{categoryId}")
	public Flux<StockConceptRelation> getRelationsByCategoryId(@PathVariable Long categoryId) {
		return relationService.getRelationsByCategoryId(categoryId);
	}

	// GET /api/concept-relations/stocks/2330/categories
	@GetMapping("/stocks/{symbol}/categories")
	public Flux<StockConceptCategory> getCategoriesBySymbol(@PathVariable String symbol) {
		return relationService.getCategoriesBySymbol(symbol);
	}

	// POST /api/concept-relations
	@PostMapping
	@ResponseStatus(HttpStatus.CREATED)
	public Mono<StockConceptRelation> createRelation(@RequestBody StockConceptRelation relation) {
		return relationService.createRelation(relation);
	}

	// PUT /api/concept-relations/{id}
	@PutMapping("/{id}")
	public Mono<StockConceptRelation> updateRelation(@PathVariable Long id, @RequestBody StockConceptRelation relation) {
		return relationService.updateRelation(id, relation);
	}

	// DELETE /api/concept-relations/{id}
	@DeleteMapping("/{id}")
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public Mono<Void> deleteRelation(@PathVariable Long id) {
		return relationService.deleteRelation(id);
	}
}
