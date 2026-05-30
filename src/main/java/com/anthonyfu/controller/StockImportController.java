package com.anthonyfu.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.anthonyfu.model.entity.Stock;
import com.anthonyfu.model.entity.StockConceptCategory;
import com.anthonyfu.repository.StockConceptCategoryRepository;
import com.anthonyfu.repository.StockConceptRelationRepository;
import com.anthonyfu.repository.StockRepository;
import com.anthonyfu.service.TwseStockImportService;

import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/import")
@RequiredArgsConstructor
public class StockImportController {

    private final TwseStockImportService         importService;
    private final StockConceptCategoryRepository  categoryRepo;
    private final StockConceptRelationRepository  relationRepo;
    private final StockRepository                 stockRepository;

    /**
     * POST /api/import/twse
     * 觸發全台股資料匯入（TWSE 上市 + TPEx 上櫃）
     * 首次執行約需 30-60 秒
     */
    @PostMapping("/twse")
    public Mono<Map<String, Object>> importAll() {
        return importService.importAll()
                .map(r -> Map.of(
                        "success",    true,
                        "twseCount",  r.twseCount(),
                        "tpexCount",  r.tpexCount(),
                        "total",      r.total(),
                        "message",    "匯入完成，共 " + r.total() + " 筆台股基本資料"
                ));
    }

    /**
     * GET /api/import/categories
     * 取得所有概念股分類
     */
    @GetMapping("/categories")
    public Flux<StockConceptCategory> getAllCategories() {
        return categoryRepo.findAllByOrderByCodeAsc();
    }

    /**
     * GET /api/import/categories/{code}/stocks
     * 依概念股分類代碼取得旗下所有股票
     * 範例：GET /api/import/categories/AI/stocks
     */
    @GetMapping("/categories/{code}/stocks")
    public Flux<Stock> getStocksByCategory(@PathVariable String code) {
        return relationRepo.findStocksByCategoryCode(code.toUpperCase());
    }

    /**
     * GET /api/import/stocks/{symbol}/categories
     * 查詢某支股票隸屬的所有概念股分類
     * 範例：GET /api/import/stocks/2330/categories
     */
    @GetMapping("/stocks/{symbol}/categories")
    public Flux<StockConceptCategory> getStockCategories(@PathVariable String symbol) {
        return stockRepository.findBySymbol(symbol.toUpperCase())
                .flatMapMany(stock -> relationRepo.findCategoriesByStockId(stock.getId()));
    }
}
