package com.chris.poker.controller;

import org.springframework.web.bind.annotation.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import com.chris.poker.dto.ApiResponse;
import com.chris.poker.dto.CreateGameRequest;
import com.chris.poker.dto.GameStateResponse;
import com.chris.poker.dto.PlayerActionRequest;
import com.chris.poker.dto.PlayerHandResponse;
import com.chris.poker.dto.TokenResponse;
import com.chris.poker.service.TokenService;
import com.chris.poker.util.TokenUtil;
import com.chris.poker.domain.Action;
import com.chris.poker.domain.GamePhase;
import com.chris.poker.domain.GameState;
import com.chris.poker.domain.Player;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/game")
@CrossOrigin(origins = "http://localhost:5173")
public class PokerGameController {

	private GameState gameState;

	@Autowired
	private TokenService tokenService;

	/*
	 * 遊戲生命週期
	 */
	@PostMapping("/create")
	public ResponseEntity<ApiResponse<?>> createGame(@RequestBody CreateGameRequest request) {

		// 由全局異常處理器捕捉回報給前端
		if (request.getPlayers().size() < 2) {
			throw new IllegalArgumentException("至少需要兩名玩家");
		}

		// 轉換成玩家類別
		List<Player> players = request.getPlayers().stream().map(dto -> new Player(dto.getName(), dto.getChips()))
				.collect(Collectors.toList());
		gameState = new GameState(players, request.getSmallBlind(), request.getBigBlind());
		tokenService.clearAllTokens();

		Map<String, String> playerTokens = new HashMap<>();
		for (Player p : players) {
			String token = tokenService.createPlayerToken(p.getName());
			playerTokens.put(p.getName(), token);
		}

		TokenResponse response = new TokenResponse(playerTokens);
		return ResponseEntity.ok(ApiResponse.success("遊戲創建成功", response));
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

		Player player = gameState.getPlayersInHand().stream().filter(p -> p.getName().equals(playerName))
				.findFirst().orElseThrow(() -> new IllegalArgumentException("找不到此玩家:" + playerName));

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

	@GetMapping("/hand")
	public ResponseEntity<ApiResponse<PlayerHandResponse>> getPlayerHand(@RequestHeader("Authorization") String authHeader) {
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
			return Action.allIn(request.getAmount());
		default:
			throw new IllegalArgumentException("無效的行動類型: " + request.getAction());
		}

	}

	// 自動推進遊戲
	private void processAutoGame() {
		while (gameState.isCurrentBettingRoundComplete()) {
			gameState.nextPhase();

//			if (gameState.canStartNextHand()) {
//				gameState.startNewHand();
//				break;
//			}
			if (gameState.getCurrentPhase() == GamePhase.SHOWDOWN) {
	            break;
	        }
		}
	}

}
