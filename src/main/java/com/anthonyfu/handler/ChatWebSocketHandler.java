package com.anthonyfu.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.socket.WebSocketHandler;
import org.springframework.web.reactive.socket.WebSocketMessage;
import org.springframework.web.reactive.socket.WebSocketSession;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.Sinks;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 直播聊天室 WebSocket Handler
 *
 * 每個直播間（roomId）有獨立的訊息廣播頻道。
 * 觀眾連線時自動加入對應房間，斷線時自動離開。
 */
@Slf4j
@Component
public class ChatWebSocketHandler implements WebSocketHandler {

    private final ObjectMapper objectMapper = new ObjectMapper();

    /** 每個直播間的訊息廣播頻道 */
    private final Map<String, Sinks.Many<String>> roomSinks = new ConcurrentHashMap<>();

    /** 每個直播間的線上人數 */
    private final Map<String, Integer> viewerCounts = new ConcurrentHashMap<>();

    @Override
    public Mono<Void> handle(WebSocketSession session) {
        // 從 URL 取得 roomId，例如 ws://localhost:8080/api/ws/chat?roomId=room001
        String roomId = extractRoomId(session);

        // 取得或建立該房間的廣播頻道
        Sinks.Many<String> sink = roomSinks.computeIfAbsent(roomId,
                k -> Sinks.many().multicast().onBackpressureBuffer());

        // 觀眾人數 +1
        viewerCounts.merge(roomId, 1, Integer::sum);

        // 接收觀眾發送的訊息 → 廣播給該房間所有人
        Mono<Void> input = session.receive()
                .map(WebSocketMessage::getPayloadAsText)
                .doOnNext(msg -> {
                    String formatted = formatMessage(msg);
                    sink.tryEmitNext(formatted);
                })
                .doFinally(signal -> {
                    // 觀眾離線，人數 -1
                    viewerCounts.merge(roomId, -1, Integer::sum);
                    if (viewerCounts.getOrDefault(roomId, 0) <= 0) {
                        viewerCounts.remove(roomId);
                        roomSinks.remove(roomId);
                    }
                })
                .then();

        // 將房間的廣播訊息推送給這位觀眾
        Flux<WebSocketMessage> output = sink.asFlux()
                .map(session::textMessage);

        return Mono.zip(input, session.send(output)).then();
    }

    public int getViewerCount(String roomId) {
        return viewerCounts.getOrDefault(roomId, 0);
    }

    private String extractRoomId(WebSocketSession session) {
        String query = session.getHandshakeInfo().getUri().getQuery();
        if (query != null && query.contains("roomId=")) {
            return query.split("roomId=")[1].split("&")[0];
        }
        return "default";
    }

    private String formatMessage(String rawMessage) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        // 預期前端送來格式: {"user":"xxx","message":"yyy"}
        // 加上時間戳後廣播
        try {
            var node = objectMapper.readTree(rawMessage);
            String user = node.has("user") ? node.get("user").asText() : "匿名";
            String message = node.has("message") ? node.get("message").asText() : rawMessage;
            return objectMapper.writeValueAsString(Map.of(
                    "user", user,
                    "message", message,
                    "time", timestamp
            ));
        } catch (Exception e) {
            log.warn("無法解析聊天訊息，將原樣廣播: {}", rawMessage, e);
            return rawMessage;
        }
    }
}
