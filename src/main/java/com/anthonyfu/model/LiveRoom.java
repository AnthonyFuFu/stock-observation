package com.anthonyfu.model;

import java.time.LocalDateTime;

/**
 * 直播間資訊
 */
public record LiveRoom(
        String roomId,
        String title,
        String streamerName,
        Status status,
        LocalDateTime startedAt,
        int viewerCount
) {
    public enum Status {
        LIVE, OFFLINE
    }

    public static LiveRoom create(String roomId, String title, String streamerName) {
        return new LiveRoom(roomId, title, streamerName,
                Status.LIVE, LocalDateTime.now(), 0);
    }

    public LiveRoom withViewerCount(int count) {
        return new LiveRoom(roomId, title, streamerName, status, startedAt, count);
    }
}
