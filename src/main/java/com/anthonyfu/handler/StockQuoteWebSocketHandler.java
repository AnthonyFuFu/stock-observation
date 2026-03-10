package com.anthonyfu.handler;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import tools.jackson.databind.ObjectMapper;

@Component
@RequiredArgsConstructor
@Slf4j
public class StockQuoteWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper;
    
    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // 這裡應該接入實際的股票行情數據源
        // 此處用模擬數據作為示例
        return session.send(
                Flux.interval(Duration.ofSeconds(1))
                    .map(sequence -> createMockQuote(sequence))
                    .map(quote -> {
                        return objectMapper.writeValueAsString(quote);
                    })
                    .map(session::textMessage)
            ).and(session.receive()
                    .map(WebSocketMessage::getPayloadAsText)
                    .log());
    }
    
    private Map<String, Object> createMockQuote(Long sequence) {
        Map<String, Object> quote = new HashMap<>();
        quote.put("symbol", "AAPL");
        quote.put("price", 150 + Math.sin(sequence / 10.0) * 2);
        quote.put("timestamp", System.currentTimeMillis());
        return quote;
    }
}