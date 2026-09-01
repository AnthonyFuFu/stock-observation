package com.anthonyfu.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.anthonyfu.model.CandlePoint;
import com.anthonyfu.model.finnhub.FinnhubCandle;
import com.anthonyfu.model.finnhub.FinnhubProfile;
import com.anthonyfu.model.finnhub.FinnhubQuote;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class FinnhubService implements StockCandleProvider {

    private final WebClient webClient;

    @Value("${finnhub.api-key}")
    private String apiKey;

    public FinnhubService(WebClient webClient) {
        this.webClient = webClient.mutate()
                .baseUrl("https://finnhub.io/api/v1")
                .build();
    }

    @Override
    public String providerKey() {
        return "finnhub";
    }

    @Override
    public Mono<List<CandlePoint>> getCandles(String symbol) {
        return getCandles(symbol, "D", 90);
    }

    public Mono<FinnhubQuote> getQuote(String symbol) {
        return webClient.get()
                .uri(uri -> uri.path("/quote")
                        .queryParam("symbol", symbol)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(FinnhubQuote.class)
                .onErrorResume(e -> {
                    log.error("Finnhub quote error [{}]", symbol, e);
                    return Mono.empty();
                });
    }

    public Mono<List<CandlePoint>> getCandles(String symbol, String resolution, int days) {
        long to = Instant.now().getEpochSecond();
        long from = Instant.now().minus(days, ChronoUnit.DAYS).getEpochSecond();

        return webClient.get()
                .uri(uri -> uri.path("/stock/candle")
                        .queryParam("symbol", symbol)
                        .queryParam("resolution", resolution)
                        .queryParam("from", from)
                        .queryParam("to", to)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(FinnhubCandle.class)
                .map(this::toCandlePoints)
                .onErrorResume(e -> {
                    log.error("Finnhub candle error [{}]", symbol, e);
                    return Mono.just(Collections.emptyList());
                });
    }

    public Mono<FinnhubProfile> getProfile(String symbol) {
        return webClient.get()
                .uri(uri -> uri.path("/stock/profile2")
                        .queryParam("symbol", symbol)
                        .queryParam("token", apiKey)
                        .build())
                .retrieve()
                .bodyToMono(FinnhubProfile.class)
                .onErrorResume(e -> {
                    log.error("Finnhub profile error [{}]", symbol, e);
                    return Mono.empty();
                });
    }

    public Mono<Map<String, FinnhubQuote>> getUsMarket() {
        List<String> symbols = Arrays.asList("SPY", "QQQ", "DIA");
        return Flux.fromIterable(symbols)
                .flatMap(sym -> getQuote(sym)
                        .defaultIfEmpty(new FinnhubQuote())
                        .map(q -> Map.entry(sym, q)))
                .collectMap(Map.Entry::getKey, Map.Entry::getValue);
    }

    private List<CandlePoint> toCandlePoints(FinnhubCandle candle) {
        List<CandlePoint> result = new ArrayList<>();
        if (candle == null || !"ok".equals(candle.getS()) || candle.getT() == null) {
            return result;
        }
        for (int i = 0; i < candle.getT().size(); i++) {
            result.add(new CandlePoint(
                    candle.getT().get(i),
                    get(candle.getO(), i),
                    get(candle.getH(), i),
                    get(candle.getL(), i),
                    get(candle.getC(), i),
                    get(candle.getV(), i)));
        }
        return result;
    }

    private Double get(List<Double> list, int i) {
        return (list != null && i < list.size()) ? list.get(i) : null;
    }
}
