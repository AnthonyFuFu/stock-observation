package com.anthonyfu.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.anthonyfu.model.CompanyInfo;
import com.anthonyfu.model.MarketInfo;
import com.anthonyfu.model.StockDailyInfo;
import com.anthonyfu.service.TwseService;

import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/twse")
public class TwseController {
	
	@Autowired
    private TwseService twseService;
    
    public TwseController(TwseService twseService) {
        this.twseService = twseService;
    }
    // http://localhost:8080/api/twse/stock/2330
    @GetMapping("/stock/{code}")
    public Mono<StockDailyInfo> getStockInfo(@PathVariable String code) {
        return twseService.getStockDailyInfo(code);
    }
    // http://localhost:8080/api/twse/market
    @GetMapping("/market")
    public Mono<MarketInfo> getMarketInfo() {
        return twseService.getMarketInfo();
    }
    // http://localhost:8080/api/twse/company?query=台積電
    @GetMapping("/company")
    public Mono<CompanyInfo> searchCompany(@RequestParam String query) {
        return twseService.getCompanyInfo(query);
    }
}