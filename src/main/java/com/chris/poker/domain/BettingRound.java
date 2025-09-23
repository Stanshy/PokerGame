package com.chris.poker.domain;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 管理德州撲克的單輪下注邏輯
 */
public class BettingRound {
	private List<Player> players; // 玩家列表
	private int currentPlayerIndex; // 當下玩家位置
	private int currentHighestBet; // 最高下注
	private int lastRaiseIncrement;              //加注增量
	private boolean isPreflop; // 是否翻牌前
	private int smallBlindAmount; // 小盲金額
	private int bigBlindAmount; // 大盲金額
	private boolean bigBlindHasActed = false;

	public BettingRound(List<Player> activePlayers, int smallBlindAmount, int bigBlindAmount, boolean isPreflop) {
		if (activePlayers.isEmpty()) {
			throw new IllegalArgumentException("Must have at least one active player");
		}
		this.players = activePlayers.stream().filter(Player::isInHand).collect(Collectors.toList());
		this.smallBlindAmount = smallBlindAmount;
		this.bigBlindAmount = bigBlindAmount;
		this.isPreflop = isPreflop;
		this.currentHighestBet = 0;
		for (Player p : activePlayers) {
			p.resetForNewBettingRound();
		}

		if (isPreflop) {
			setupPreflop();
		} else {
			setupPostflop();
		}
	}

	// 初始化
	private void setupPreflop() {
		Player smallBlindPlayer = null;
		Player bigBlindPlayer = null;

		for (Player p : players) {
			if (p.isSmallBlind()) {
				smallBlindPlayer = p;
			}
			if (p.isBigBlind()) {
				bigBlindPlayer = p;
			}
		}
		boolean sbAllIn = !smallBlindPlayer.bet(smallBlindAmount);
		boolean bbAllIn = !bigBlindPlayer.bet(bigBlindAmount);
		
		smallBlindPlayer.resetLastActionOnly();
	    bigBlindPlayer.resetLastActionOnly();
	    
		if (sbAllIn) {
			smallBlindPlayer.allin();
		}
		if (bbAllIn) {
			bigBlindPlayer.allin();
		}
		currentHighestBet = bigBlindAmount;

		int bigBlindIndex = players.indexOf(bigBlindPlayer);
		currentPlayerIndex = (bigBlindIndex + 1) % players.size(); // 環狀座位

		if (!players.get(currentPlayerIndex).canAct()) {
			moveToNextPlayer();
		}
	}

	private void setupPostflop() {
		Player smallBlindPlayer = null;
		for (Player p : players) {
			if (p.isSmallBlind()) {
				smallBlindPlayer = p;
				break;
			}
		}
		if (smallBlindPlayer != null) {
			currentPlayerIndex = players.indexOf(smallBlindPlayer);
		} else {
			// 如果小盲不在，從第一個活躍玩家開始
			currentPlayerIndex = 0;
		}

		this.currentHighestBet = 0;
	}

	public boolean executeAction(Player player, Action action) {
		if (!isCurrentPlayer(player)) {
			return false;
		}

		if (isPreflop && player.isBigBlind()) {
			bigBlindHasActed = true;
		}
		boolean success = false;

		switch (action.getType()) {
		case FOLD:
			player.fold();
			success = true;
			break;
		case CHECK:
			if (currentHighestBet == player.getCurrentBet()) {
				success = player.check();
			}
			break;
		case CALL:
			success = player.call(currentHighestBet);
			break;
		case BET:
			if (action.getAmount() < bigBlindAmount) {
		        return false;
		    }
			success = player.bet(action.getAmount());
			if (success) {
				currentHighestBet = player.getCurrentBet();
				lastRaiseIncrement =0;
			}
			break;
		case RAISE:
			if(action.getAmount()<getMinRaiseAmount()) {
				return false;
			}
			System.out.println(getMinRaiseAmount());
			success = player.raise(action.getAmount());
			if (success) {
				int newRaiseIncrement = player.getCurrentBet() -currentHighestBet;
				lastRaiseIncrement  = newRaiseIncrement;
				currentHighestBet = player.getCurrentBet();
			}
			break;
		case ALL_IN:
			success = player.allin();
			if (success && player.getCurrentBet() > currentHighestBet) {
				currentHighestBet = player.getCurrentBet();
			}
			break;
		}
		if (success) {
			moveToNextPlayer();
		}
		return success;
	}

	public boolean isRoundComplete() {
		
		// 剩一名玩家判斷結束
		List<Player> playersInHand = players.stream().filter(Player::isInHand).collect(Collectors.toList());
		List<Player> activePlayers = playersInHand.stream().filter(Player::canAct).collect(Collectors.toList());
		
		//只剩一名玩家
		if (playersInHand.size() <= 1) {
			return true;
		}
		// 玩家都不可動作判斷結束
		if (activePlayers.isEmpty()) {
			return true;
		}
		
		//行動過後才會更新none
		boolean allPlayersHaveActed = activePlayers.stream().allMatch(Player::hasActed);


		if (!allPlayersHaveActed) {
			return false; // 還有人沒行動，不能結束
		}
		// 所有可動作玩家下注金額也相同判斷結束
		for (Player player : activePlayers) {
			if (player.getCurrentBet() != currentHighestBet) {
				return false;
			}
		}
		//大忙要動作過一次 不然前面會判斷結束
		if (isPreflop && !bigBlindHasActed) {
			return false;
		}
		return true;
	}

	// 找當前玩家
	public Player getCurrentPlayer() {
		if (isRoundComplete()) {
			return null;
		}
		return players.get(currentPlayerIndex);
	}

	// 找下一個可開始動作的玩家
	private void moveToNextPlayer() {
		int temp = 0;
		do {
			currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
			temp++;
			if (temp > players.size()) {
				currentPlayerIndex = -1;
				break;
			}
		} while (!players.get(currentPlayerIndex).canAct());
	}
	
	//獲取加注增量
	public int getMinRaiseAmount() {
		if(lastRaiseIncrement ==0) {
			return currentHighestBet *2;
		}else {
			return currentHighestBet+lastRaiseIncrement;
		}
	}
	
	
	//獲取當前玩家的合法動作
	public List<PlayerAction> getLegalActions(){
		Player currentPlayer  = getCurrentPlayer();
		if(currentPlayer == null) {
			return Collections.emptyList();
		}
		
		List<PlayerAction> legalAction = new ArrayList<>();
		
		if(currentPlayer.canAct()) {
			legalAction.add(PlayerAction.FOLD);
		}
		
		if(currentPlayer.getCurrentBet() == currentHighestBet) {
			legalAction.add(PlayerAction.CHECK);
		}
		
		if(currentPlayer.getCurrentBet()<currentHighestBet && currentPlayer.getChips()>0) {
			legalAction.add(PlayerAction.CALL);
		}
		
		if(currentHighestBet == 0 && currentPlayer.getChips()>0) {
			legalAction.add(PlayerAction.BET);
		}
		
		if(currentHighestBet>0 && currentPlayer.getChips()>= getMinRaiseAmount()-currentPlayer.getCurrentBet()) {
			legalAction.add(PlayerAction.RAISE);
		}
		
		if(currentPlayer.getChips()>0) {
			legalAction.add(PlayerAction.ALL_IN);
		}
		
		return legalAction;
	}

	// 簡單驗證
	private boolean isCurrentPlayer(Player player) {
		return players.get(currentPlayerIndex).equals(player);
	}

	public List<Player> getPlayersInHand() {
		return players.stream().filter(Player::isInHand).collect(Collectors.toList());
	}

	public int getCurrentHighestBet() {
		return currentHighestBet;
	}

	public List<Player> getPlayers() {
		return new ArrayList<>(players);
	}
}