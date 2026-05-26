package com.anthonyfu.config;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.HandlerMapping;
import org.springframework.web.reactive.handler.SimpleUrlHandlerMapping;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.server.support.WebSocketHandlerAdapter;

import com.anthonyfu.handler.ChatWebSocketHandler;
import com.anthonyfu.handler.StockQuoteWebSocketHandler;

@Configuration
public class WebSocketConfig {

    @Bean
    HandlerMapping handlerMapping(StockQuoteWebSocketHandler stockQuoteWebSocketHandler) {
        Map<String, WebSocketHandler> map = new HashMap<>();
        map.put("/ws/quotes", stockQuoteWebSocketHandler);
        
        SimpleUrlHandlerMapping handlerMapping = new SimpleUrlHandlerMapping();
        handlerMapping.setUrlMap(map);
        handlerMapping.setOrder(-1); // Before annotated controllers
        return handlerMapping;
    }

    @Bean
    HandlerMapping webSocketMapping(ChatWebSocketHandler chatHandler) {
        // 注意：base-path=/api 會自動套用，所以前端連 ws://localhost:8080/api/ws/chat
        return new SimpleUrlHandlerMapping(Map.of(
                "/ws/chat", chatHandler
        ), 1);
    }
    
    @Bean
    WebSocketHandlerAdapter handlerAdapter() {
        return new WebSocketHandlerAdapter();
    }
}
