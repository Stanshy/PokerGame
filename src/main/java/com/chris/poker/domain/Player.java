package com.chris.poker.domain;

import com.chris.poker.card.Hand;

public class Player {
	private final String name;
	private Hand hand;
	private int chips;
	private int currentBet; // 本輪已下注金額
	private int totalBetThisHand; // 這手牌總共下注金額
	private PlayerAction lastAction;
	private PlayerStatus status;
	private boolean isButton;
	private boolean isSmallBlind;
	private boolean isBigBlind;

	public Player(String name, int chips) {
		this.name = name;
		this.chips = chips;
		this.currentBet = 0;
		this.totalBetThisHand = 0;
		this.status = chips > 0 ? PlayerStatus.ACTIVE : PlayerStatus.OUT_OF_GAME;
		this.lastAction = PlayerAction.NONE;
		this.isBigBlind = false;
		this.isButton = false;
		this.isSmallBlind = false;
	}

	// 棄牌
	public void fold() {
		if (!canAct()) {
			throw new IllegalStateException("不能行動");
		}
		this.status = PlayerStatus.FOLDED;
		this.lastAction = PlayerAction.FOLD;
	}

	// 過牌
	public boolean check() {
		if (!canAct()) {
			return false;
		}
		this.lastAction = PlayerAction.CHECK;
		return true;
	}

	// 跟住
	public boolean call(int amount) {
		if (!canAct()) {
			return false;
		}
		int additionalBet = amount - currentBet;
		if (additionalBet < 0) {
			throw new IllegalStateException("跟注方法呼叫錯誤");
		}
		if (chips <= additionalBet) {
			return allin();
		}
		chips -= additionalBet;
		currentBet = amount;
		totalBetThisHand += additionalBet;
		this.lastAction = PlayerAction.CALL;

		return true;
	}

	// 下注
	public boolean bet(int amount) {
		if (!canAct()) {
			return false;
		}
		if (chips < amount) {
			return false;
		}
		chips -= amount;
		currentBet = amount;
		totalBetThisHand += amount;
		this.lastAction = PlayerAction.BET;
		if (chips == 0) {
			this.status = PlayerStatus.ALL_IN;
		}
		return true;
	}

	// 加注
	public boolean raise(int amount) {
		if (!canAct()) {
			return false;
		}
		int additionalBet = amount - currentBet;
		if (additionalBet <= 0 || chips < additionalBet)
			return false;
		chips -= additionalBet;
		currentBet = amount;
		totalBetThisHand += additionalBet;
		this.lastAction = PlayerAction.RAISE;
		if (chips == 0) {
			this.status = PlayerStatus.ALL_IN;
		}
		return true;
	}

	// 全下
	public boolean allin() {
		if (!canAct()) {
			return false;
		}
		if (chips == 0) {
			return false;
		}
		currentBet += chips;
		totalBetThisHand += chips;
		chips = 0;
		this.status = PlayerStatus.ALL_IN;
		this.lastAction = PlayerAction.ALL_IN;
		return true;
	}

	public boolean performAction(Action action) {
		switch (action.getType()) {
		case FOLD:
			fold();
			return true;
		case CHECK:
			return check();
		case CALL:
			return call(action.getAmount());
		case BET:
			return bet(action.getAmount());
		case RAISE:
			return raise(action.getAmount());
		case ALL_IN:
			return allin();
		default:
			return false;
		}
	}

	// 重置每手牌的狀態
	public void resetForNewHand() {
		this.hand = null;
		this.currentBet = 0;
		this.totalBetThisHand = 0;
		this.lastAction = PlayerAction.NONE;
		if (this.status != PlayerStatus.OUT_OF_GAME) {
			this.status = PlayerStatus.ACTIVE;
		}
		this.isButton = false;
		this.isSmallBlind = false;
		this.isBigBlind = false;
	}

	// 重置每個下注輪的狀態
	public void resetForNewBettingRound() {
		this.currentBet = 0;
		this.lastAction = PlayerAction.NONE;
	}
	//給大小盲用
	public void resetLastActionOnly() {
	    this.lastAction = PlayerAction.NONE;
	}

	// 獲得贏的籌碼
	public void winChips(int amount) {
		this.chips += amount;
		if (this.status == PlayerStatus.OUT_OF_GAME && this.chips > 0) {
			this.status = PlayerStatus.ACTIVE;
		}
	}

	// === Getters ===
	public String getName() {
		return name;
	}

	public Hand getHand() {
		return hand;
	}

	public int getChips() {
		return chips;
	}

	public int getCurrentBet() {
		return currentBet;
	}

	public int getTotalBetThisHand() {
		return totalBetThisHand;
	}

	public PlayerAction getLastAction() {
		return lastAction;
	}

	public PlayerStatus getStatus() {
		return status;
	}

	public boolean isButton() {
		return isButton;
	}

	public boolean isSmallBlind() {
		return isSmallBlind;
	}

	public boolean isBigBlind() {
		return isBigBlind;
	}

	// === Setters ===
	public void setHand(Hand hand) {
		this.hand = hand;
	}

	public void setButton(boolean button) {
		this.isButton = button;
	}

	public void setSmallBlind(boolean smallBlind) {
		this.isSmallBlind = smallBlind;
	}

	public void setBigBlind(boolean bigBlind) {
		this.isBigBlind = bigBlind;
	}

	/**
	 * 玩家判斷方法
	 */

	// 是否處於可動作狀態
	public boolean canAct() {
		return status == PlayerStatus.ACTIVE;
	}
	
	//是否在本局裡面
	public boolean isInHand() {
		return status != PlayerStatus.FOLDED && status != PlayerStatus.OUT_OF_GAME;
	}

	// 是否已經行動過
	public boolean hasActed() {
		return lastAction != PlayerAction.NONE;
	}

	// 是否全下
	public boolean isAllIn() {
		return status == PlayerStatus.ALL_IN;
	}

	// 是否棄牌
	public boolean hasFolded() {
		return status == PlayerStatus.FOLDED;
	}

	// 獲取可用籌碼
	public int getAvailableChips() {
		return chips;
	}

	// 總籌碼 包含下注
	public int getTotalChips() {
		return chips + currentBet;
	}

	/**
	 * 玩家狀態枚舉
	 */
	public enum PlayerStatus {
		ACTIVE, // 可以正常行動
		FOLDED, // 已棄牌
		ALL_IN, // 全押
		OUT_OF_GAME // 沒有籌碼，退出遊戲
		
		
	}
}