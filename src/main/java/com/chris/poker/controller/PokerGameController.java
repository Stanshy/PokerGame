package com.chris.poker.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.chris.poker.dto.ApiResponse;
import com.chris.poker.dto.TokenResponse;
import com.chris.poker.dto.game.CreateGameRequest;
import com.chris.poker.dto.game.GameStateResponse;
import com.chris.poker.dto.game.HandHistoryResponse;
import com.chris.poker.dto.player.PlayerActionRequest;
import com.chris.poker.dto.player.PlayerHandResponse;
import com.chris.poker.dto.seat.JoinSeatRequest;
import com.chris.poker.dto.seat.JoinSeatResponse;
import com.chris.poker.dto.seat.LeaveSeatRequest;
import com.chris.poker.dto.seat.SeatsStateResponse;
import com.chris.poker.seat.SeatInfo;
import com.chris.poker.seat.SeatManager;
import com.chris.poker.service.GameBroadcastService;
import com.chris.poker.service.TokenService;
import com.chris.poker.util.TokenUtil;
import com.chris.poker.domain.Action;
import com.chris.poker.domain.GamePhase;
import com.chris.poker.domain.GameState;
import com.chris.poker.domain.HandHistory;
import com.chris.poker.domain.Player;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
public class PokerGameController {

	private GameState gameState;

	@Autowired
	private TokenService tokenService;

	@Autowired
	private GameBroadcastService broadcastService;

	@Autowired
	private SeatManager seatManager;

	/*
	 * 遊戲生命週期
	 */
	@PostMapping("/create")
	public ResponseEntity<ApiResponse<?>> createGame(@RequestBody CreateGameRequest request) {

		List<Player> players = seatManager.creatPlayersList();
		if (players.size() < 2) {
			throw new IllegalArgumentException("至少需要兩名玩家");
		}

		gameState = new GameState(players, request.getSmallBlind(), request.getBigBlind());

		Map<String, String> playerTokens = new HashMap<>();
		for (Player p : players) {
			String token = tokenService.createPlayerToken(p.getName());
			playerTokens.put(p.getName(), token);
		}
		seatManager.clear(); // 創建完 座位管理就不需要資訊了
		return ResponseEntity.ok(ApiResponse.success("遊戲創建成功"));
	}

	@PostMapping("/start-hand")
	public ResponseEntity<ApiResponse<String>> startNewHand() {

		if (gameState == null) {
			throw new IllegalStateException("遊戲尚未創建");
		}

		if (!gameState.canStartNextHand()) {
			throw new IllegalStateException("當前手牌尚未結束，無法開始新手牌");
		}
		gameState.startNewHand();
		broadcastService.broadcastGameState(gameState);

		return ResponseEntity.ok(ApiResponse.success("新手牌開始"));

	}

	@PostMapping("/end")
	public ResponseEntity<ApiResponse<String>> endGame() {
		gameState = null;
		return ResponseEntity.ok(ApiResponse.success("遊戲已結束"));
	}

	@PostMapping("/restart")
	public ResponseEntity<ApiResponse<?>> restartGame(@RequestBody CreateGameRequest request) {
		return createGame(request);

	}

	/*
	 * 遊戲邏輯
	 */

	@PostMapping("/action")
	public ResponseEntity<ApiResponse<Map<String, Object>>> executePlayerAction(
			@RequestHeader("Authorization") String authHeader, @RequestBody PlayerActionRequest request) {
		if (gameState == null) {
			throw new IllegalStateException("遊戲尚未創建");
		}

		String playerName = TokenUtil.getPlayerName(authHeader, tokenService);

		Player player = gameState.getPlayersInHand().stream().filter(p -> p.getName().equals(playerName)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("找不到此玩家:" + playerName));

		Player currentPlayer = gameState.getCurrentPlayer();
		if (currentPlayer == null || !currentPlayer.equals(player)) {
			throw new IllegalStateException("不是該玩家的回合");
		}

		Action action = createAction(request);

		boolean success = gameState.executePlayerAction(currentPlayer, action);
		if (!success) {
			throw new IllegalStateException("無效行動");
		}

		processAutoGame();
		broadcastService.broadcastGameState(gameState);

		Map<String, Object> result = new HashMap<>();
		result.put("action", request.getAction());
		result.put("player", playerName);
		result.put("currentPhase", gameState.getCurrentPhase().name());

		return ResponseEntity.ok(ApiResponse.success("行動執行成功", result));
	}

	/*
	 * 狀態查詢
	 */

	@GetMapping("/state")
	public ResponseEntity<ApiResponse<GameStateResponse>> getGameState() {

		if (gameState == null) {
			throw new IllegalStateException("遊戲尚未創建");
		}

		GameStateResponse res = GameStateResponse.fromEntity(gameState);
		return ResponseEntity.ok(ApiResponse.success(res));

	}

	// 玩家查看手牌
	@GetMapping("/hand")
	public ResponseEntity<ApiResponse<PlayerHandResponse>> getPlayerHand(
			@RequestHeader("Authorization") String authHeader) {
		if (gameState == null) {
			throw new IllegalStateException("遊戲尚未創建");
		}
		String playerName = TokenUtil.getPlayerName(authHeader, tokenService);
		Player player = gameState.getPlayersInHand().stream().filter(p -> p.getName().equals(playerName)).findFirst()
				.orElseThrow(() -> new IllegalArgumentException("找不到玩家:" + playerName));

		if (player.getHand() == null) {
			throw new IllegalArgumentException("尚未發牌");
		}

		PlayerHandResponse reponse = PlayerHandResponse.fromEntity(player.getHand());
		return ResponseEntity.ok(ApiResponse.success(reponse));

	}

	/*
	 * 座位管理
	 **/

	// 加入座位
	@PostMapping("/join-seat")
	public ResponseEntity<ApiResponse<JoinSeatResponse>> joinSeat(@RequestBody JoinSeatRequest request) {
		SeatInfo seatInfo = seatManager.joinSeat(request.getSeatNumber(), request.getPlayerName(), request.getChips());

		String token = tokenService.createPlayerToken(request.getPlayerName());
		broadcastService.broadcastSeatsUpdate(seatManager.getAllSeats());
		
		JoinSeatResponse response = new JoinSeatResponse(seatInfo, token);
		
		return ResponseEntity.ok(ApiResponse.success("入座成功", response));
	}

	// 離開座位
	@PostMapping("/leave-seat")
	public ResponseEntity<ApiResponse<String>> leaveSeat(@RequestBody LeaveSeatRequest request) {
		seatManager.leaveSeat(request.getSeatNumber());

		broadcastService.broadcastSeatsUpdate(seatManager.getAllSeats());

		return ResponseEntity.ok(ApiResponse.success("離座成功"));
	}

	// 獲取座位資訊
	@GetMapping("/seats")
	public ResponseEntity<ApiResponse<SeatsStateResponse>> getSeats() {
		Map<Integer, SeatInfo> seats = seatManager.getAllSeats();
		int playerCount = seats.size();
		boolean canStartGame = playerCount >= 2;
		SeatsStateResponse response = new SeatsStateResponse(seats, playerCount, canStartGame);
		return ResponseEntity.ok(ApiResponse.success(response));
	}
	
	/*
	 * 牌局紀錄
	 * */
	//獲取牌局紀錄
	@GetMapping("/history")
	public ResponseEntity<ApiResponse<List<HandHistoryResponse>>> getHandHistory(){
		
		List<HandHistoryResponse> histories = gameState.getHandHistories().stream().map(HandHistoryResponse::from).collect(Collectors.toList());
		
		return ResponseEntity.ok(ApiResponse.success(histories));
	}
	
	
	//獲取特定手牌紀錄
	@GetMapping("/history/{handId}")
	public ResponseEntity<ApiResponse<HandHistoryResponse>> getHandDetail(@PathVariable int handId){
		
		HandHistory history = gameState.getHandHistories().stream()
				.filter(h->h.getHandNumber() == handId).findFirst().orElseThrow(()-> new IllegalArgumentException("找不到手牌:"+handId));
		
		return ResponseEntity.ok(ApiResponse.success(HandHistoryResponse.from(history)));
	}
	

	// 輔助方法

	// 創建行動
	private Action createAction(PlayerActionRequest request) {

		switch (request.getAction()) {

		case "FOLD":
			return Action.fold();
		case "CHECK":
			return Action.check();
		case "CALL":
			return Action.call(request.getAmount());
		case "BET":
			return Action.bet(request.getAmount());
		case "RAISE":
			return Action.raise(request.getAmount());
		case "ALL_IN":
			return Action.allIn();
		default:
			throw new IllegalArgumentException("無效的行動類型: " + request.getAction());
		}

	}

	// 自動推進遊戲
	private void processAutoGame() {
		while (gameState.isCurrentBettingRoundComplete()) {
			gameState.nextPhase();

			if (gameState.getCurrentPhase() == GamePhase.SHOWDOWN) {
				break;
			}
		}
	}

}
