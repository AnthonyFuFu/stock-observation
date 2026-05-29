package com.anthonyfu.service;

import java.nio.charset.Charset;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import com.anthonyfu.model.Stock;
import com.anthonyfu.repository.StockRepository;

import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * 從 TWSE ISIN 頁面批次匯入全台股基本資料（約 1700+ 支）
 *
 * 資料來源：https://isin.twse.com.tw/isin/C_public.jsp
 *   strMode=2  → 台灣證券交易所（TWSE 上市）
 *   strMode=4  → 證券櫃檯買賣中心（TPEx 上櫃）
 *
 * 頁面使用 Big5 編碼，需手動轉換。
 */
@Slf4j
@Service
public class TwseStockImportService {

    private static final String ISIN_BASE = "https://isin.twse.com.tw/isin/C_public.jsp";
    private static final Charset BIG5     = Charset.forName("Big5");
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("yyyy/MM/dd");

    // 只匯入普通股票（4~6碼）；排除衍生品
    private static final Pattern VALID_CODE   = Pattern.compile("^[0-9]{4,6}[A-Z]?$");
    private static final Pattern TD_PATTERN   = Pattern.compile("<td[^>]*>\\s*(?:&nbsp;)?([^<]*?)\\s*</td>");
    private static final Pattern ROW_PATTERN  = Pattern.compile("<tr bgcolor=\"#(?:FFFFFF|FAFAD2)\"[^>]*>(.*?)</tr>",
            Pattern.DOTALL | Pattern.CASE_INSENSITIVE);

    // 不匯入的種類（過濾關鍵字）
    private static final List<String> SKIP_KEYWORDS = List.of(
            "債", "認購", "認售", "特別股", "受益憑證", "存託憑證", "warrant",
            "TDR", "REITs", "基金", "指數投資信託"
    );

    private final WebClient  webClient;
    private final StockRepository stockRepository;

    public TwseStockImportService(WebClient webClient, StockRepository stockRepository) {
        this.webClient       = webClient.mutate().build();
        this.stockRepository = stockRepository;
    }

    /** 匯入上市 + 上櫃，回傳實際寫入筆數 */
    public Mono<ImportResult> importAll() {
        log.info("開始匯入全台股基本資料...");
        return Mono.zip(
                        importFromIsin("TWSE", 2),
                        importFromIsin("TPEx", 4))
                .map(t -> new ImportResult(t.getT1(), t.getT2()))
                .doOnSuccess(r -> log.info("匯入完成 → 上市 {} 筆 / 上櫃 {} 筆 / 合計 {} 筆",
                        r.twseCount(), r.tpexCount(), r.total()));
    }

    // ── 單一市場匯入 ─────────────────────────────────────────────

    private Mono<Integer> importFromIsin(String marketType, int strMode) {
        return webClient.get()
                .uri(u -> u.scheme("https").host("isin.twse.com.tw")
                        .path("/isin/C_public.jsp")
                        .queryParam("strMode", strMode)
                        .build())
                .retrieve()
                .bodyToMono(byte[].class)
                .map(bytes -> new String(bytes, BIG5))
                .flatMapMany(html -> Flux.fromIterable(parseHtml(html, marketType)))
                .flatMap(this::upsert)
                .count()
                .map(Long::intValue)
                .doOnSuccess(n  -> log.info("  {} 匯入 {} 筆", marketType, n))
                .onErrorResume(e -> {
                    log.error("匯入 {} 失敗：{}", marketType, e.getMessage());
                    return Mono.just(0);
                });
    }

    // ── HTML 解析 ────────────────────────────────────────────────

    List<Stock> parseHtml(String html, String marketType) {
        List<Stock> result = new ArrayList<>();
        Matcher rowMatcher = ROW_PATTERN.matcher(html);

        while (rowMatcher.find()) {
            String rowHtml = rowMatcher.group(1);
            List<String> cells = extractCells(rowHtml);
            if (cells.size() < 3) continue;

            // cell[0]: "1101 台泥" or "1101　台泥"（全形空白）
            String codeAndName = cells.get(0).replaceAll("[\\u00a0\\u3000]", " ").trim();
            int sep = codeAndName.indexOf(' ');
            if (sep <= 0) continue;

            String symbol = codeAndName.substring(0, sep).trim();
            String name   = codeAndName.substring(sep + 1).trim();

            if (!VALID_CODE.matcher(symbol).matches()) continue;
            if (shouldSkip(name)) continue;

            Stock stock = Stock.builder()
                    .symbol(symbol)
                    .name(name)
                    .exchange(marketType)
                    .marketType(marketType)
                    .currency("TWD")
                    .parValue(java.math.BigDecimal.TEN)
                    .isActive(true)
                    .build();

            // cell[1]: ISIN (TW0001101004)
            if (cells.size() > 1 && cells.get(1).startsWith("TW")) {
                stock.setIsinCode(cells.get(1));
            }

            // cell[2]: 上市日 (2024/01/02)
            if (cells.size() > 2) {
                parseDate(cells.get(2)).ifPresent(stock::setListedDate);
            }

            // 搜尋產業別（找第一個結尾為「業」或「ETF」「指數」的欄位）
            for (int i = 3; i < cells.size(); i++) {
                String cell = cells.get(i);
                if (cell.endsWith("業") || cell.equals("ETF") || cell.contains("指數")) {
                    stock.setSector(cell);
                    break;
                }
            }

            result.add(stock);
        }
        return result;
    }

    private List<String> extractCells(String rowHtml) {
        List<String> cells = new ArrayList<>();
        Matcher m = TD_PATTERN.matcher(rowHtml);
        while (m.find()) {
            cells.add(m.group(1).trim());
        }
        return cells;
    }

    private boolean shouldSkip(String name) {
        return SKIP_KEYWORDS.stream().anyMatch(name::contains);
    }

    private java.util.Optional<LocalDate> parseDate(String s) {
        if (s == null || s.isBlank()) return java.util.Optional.empty();
        try {
            return java.util.Optional.of(LocalDate.parse(s.trim(), DATE_FMT));
        } catch (Exception e) {
            return java.util.Optional.empty();
        }
    }

    // ── DB upsert（存在則更新 name/sector/isin，不覆蓋已有資料）──

    private Mono<Stock> upsert(Stock incoming) {
        return stockRepository.findBySymbol(incoming.getSymbol())
                .flatMap(existing -> {
                    existing.setName(incoming.getName());
                    existing.setExchange(incoming.getExchange());
                    existing.setMarketType(incoming.getMarketType());
                    existing.setIsActive(true);
                    if (incoming.getIsinCode()   != null) existing.setIsinCode(incoming.getIsinCode());
                    if (incoming.getListedDate() != null) existing.setListedDate(incoming.getListedDate());
                    if (incoming.getSector()     != null) existing.setSector(incoming.getSector());
                    return stockRepository.save(existing);
                })
                .switchIfEmpty(Mono.defer(() -> stockRepository.save(incoming)));
    }

    // ── 回傳結果 DTO ─────────────────────────────────────────────

    public record ImportResult(int twseCount, int tpexCount) {
        public int total() { return twseCount + tpexCount; }
    }
}
