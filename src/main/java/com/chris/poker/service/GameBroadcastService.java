package com.chris.poker.service;

import com.chris.poker.domain.GameState;
import com.chris.poker.dto.WebSocketMessage;
import com.chris.poker.dto.game.GameStateResponse;
import com.chris.poker.dto.seat.SeatsStateResponse;
import com.chris.poker.seat.SeatInfo;
import com.chris.poker.websocket.GameWebSocketHandler;

import java.util.Map;

import org.springframework.stereotype.Service;

@Service
public class GameBroadcastService {

	private final GameWebSocketHandler webSocketHandler;

	public GameBroadcastService(GameWebSocketHandler webSocketHandler) {
		this.webSocketHandler = webSocketHandler;
	}

	// 廣播遊戲狀態更新
	public void broadcastGameState(GameState gameState) {
		GameStateResponse response = GameStateResponse.fromEntity(gameState);
		WebSocketMessage message = WebSocketMessage.gameStateUpdate(response);
		webSocketHandler.broadcast(message);
	}

	// 廣播座位狀態更新
	public void broadcastSeatsUpdate(Map<Integer, SeatInfo> seats) {
	    int playerCount = seats.size();
	    boolean canStartGame = playerCount >= 2;
	    
	    SeatsStateResponse response = new SeatsStateResponse(seats, playerCount, canStartGame);
	    WebSocketMessage message = new WebSocketMessage("SEATS_UPDATE", response);
	    webSocketHandler.broadcast(message);
	}

}