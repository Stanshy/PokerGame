package com.chris.poker.dto;

import com.chris.poker.domain.Player;

public class PlayerResponse {
    private String name;
    private int chips;
    private int currentBet;
    private int totalBetThisHand;
    private String lastAction;
    private String status;
    private boolean isButton;
    private boolean isSmallBlind;
    private boolean isBigBlind;
    
    
    public static PlayerResponse fromEntity(Player player) {
        PlayerResponse response = new PlayerResponse();
        
        response.name = player.getName();
        response.chips = player.getChips();
        response.currentBet = player.getCurrentBet();
        response.totalBetThisHand = player.getTotalBetThisHand();
        response.lastAction = player.getLastAction().name();
        response.status = player.getStatus().name();
        response.isButton = player.isButton();
        response.isSmallBlind = player.isSmallBlind();
        response.isBigBlind = player.isBigBlind();
        
        return response;
    }
    
    
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public int getChips() {
		return chips;
	}
	public void setChips(int chips) {
		this.chips = chips;
	}
	public int getCurrentBet() {
		return currentBet;
	}
	public void setCurrentBet(int currentBet) {
		this.currentBet = currentBet;
	}
	public int getTotalBetThisHand() {
		return totalBetThisHand;
	}
	public void setTotalBetThisHand(int totalBetThisHand) {
		this.totalBetThisHand = totalBetThisHand;
	}
	public String getLastAction() {
		return lastAction;
	}
	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public boolean isButton() {
		return isButton;
	}
	public void setButton(boolean isButton) {
		this.isButton = isButton;
	}
	public boolean isSmallBlind() {
		return isSmallBlind;
	}
	public void setSmallBlind(boolean isSmallBlind) {
		this.isSmallBlind = isSmallBlind;
	}
	public boolean isBigBlind() {
		return isBigBlind;
	}
	public void setBigBlind(boolean isBigBlind) {
		this.isBigBlind = isBigBlind;
	}
    
    
}