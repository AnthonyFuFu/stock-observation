package com.anthonyfu.controller;

import com.anthonyfu.handler.ChatWebSocketHandler;
import com.anthonyfu.model.LiveRoom;
import com.anthonyfu.service.LiveRoomService;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/rooms")
public class LiveRoomController {

	private final LiveRoomService liveRoomService;
	private final ChatWebSocketHandler chatHandler;

	public LiveRoomController(LiveRoomService liveRoomService, ChatWebSocketHandler chatHandler) {
		this.liveRoomService = liveRoomService;
		this.chatHandler = chatHandler;
	}

	/** 建立直播間 */
	@PostMapping
	public Mono<LiveRoom> createRoom(@RequestParam String roomId, @RequestParam String title, @RequestParam String streamer) {
		return liveRoomService.createRoom(roomId, title, streamer);
	}

	/** 取得所有上線中的直播間 */
	@GetMapping
	public Flux<LiveRoom> listRooms() {
		return liveRoomService.getLiveRooms().map(room -> room.withViewerCount(chatHandler.getViewerCount(room.roomId())));
	}

	/** 取得特定直播間資訊 */
	@GetMapping("/{roomId}")
	public Mono<LiveRoom> getRoom(@PathVariable String roomId) {
		return liveRoomService.getRoom(roomId).map(room -> room.withViewerCount(chatHandler.getViewerCount(roomId)));
	}

	/** 關閉直播間 */
	@DeleteMapping("/{roomId}")
	public Mono<Void> closeRoom(@PathVariable String roomId) {
		return liveRoomService.closeRoom(roomId);
	}
}
