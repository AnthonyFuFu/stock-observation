package com.anthonyfu.service;

import com.anthonyfu.model.LiveRoom;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.concurrent.ConcurrentHashMap;

/**
 * 直播間管理服務
 * 管理直播間的建立、查詢、上下線
 */
@Service
public class LiveRoomService {

    private final ConcurrentHashMap<String, LiveRoom> rooms = new ConcurrentHashMap<>();

    /** 建立直播間 */
    public Mono<LiveRoom> createRoom(String roomId, String title, String streamerName) {
        return Mono.fromSupplier(() -> {
            LiveRoom room = LiveRoom.create(roomId, title, streamerName);
            rooms.put(roomId, room);
            return room;
        });
    }

    /** 取得直播間資訊 */
    public Mono<LiveRoom> getRoom(String roomId) {
        return Mono.justOrEmpty(rooms.get(roomId))
                .switchIfEmpty(Mono.error(new RuntimeException("直播間不存在: " + roomId)));
    }

    /** 取得所有上線中的直播間 */
    public Flux<LiveRoom> getLiveRooms() {
        return Flux.fromIterable(rooms.values())
                .filter(room -> room.status() == LiveRoom.Status.LIVE);
    }

    /** 更新觀看人數 */
    public Mono<LiveRoom> updateViewerCount(String roomId, int count) {
        return getRoom(roomId)
                .map(room -> {
                    LiveRoom updated = room.withViewerCount(count);
                    rooms.put(roomId, updated);
                    return updated;
                });
    }

    /** 關閉直播間 */
    public Mono<Void> closeRoom(String roomId) {
        return Mono.fromRunnable(() -> rooms.remove(roomId));
    }
}
