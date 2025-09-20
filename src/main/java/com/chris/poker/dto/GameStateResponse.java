package com.chris.poker.dto;

import java.util.List;
import java.util.stream.Collectors;

import com.chris.poker.domain.GameState;
import com.chris.poker.domain.Player;

public class GameStateResponse {
    private String currentPhase;
    private List<PlayerResponse> players;
    private List<String> board;
    private PotResponse pot;
    private String currentPlayer;
    private int currentHighestBet;
    private int smallBlindAmount;
    private int bigBlindAmount;
    private int buttonIndex;
    private boolean bettingRoundComplete;
    
    
    
    //靜態轉換方法
    public static GameStateResponse fromEntity(GameState gameState) {
        GameStateResponse response = new GameStateResponse();
        
        response.currentPhase = gameState.getCurrentPhase().name();
        response.smallBlindAmount = gameState.getSmallBlindAmount();
        response.bigBlindAmount = gameState.getBigBlindAmount();
        response.buttonIndex = gameState.getButtonIndex();
        response.currentHighestBet = gameState.getCurrentHighestBet();
        response.bettingRoundComplete = gameState.isCurrentBettingRoundComplete();
        
        Player currentPlayer = gameState.getCurrentPlayer();
        response.currentPlayer = currentPlayer != null ? currentPlayer.getName() : null;
        
        response.players = gameState.getPlayersInHand().stream()
            .map(PlayerResponse::fromEntity)
            .collect(Collectors.toList());
        
        response.board = gameState.getBoard().getCards().stream()
            .map(card -> card.getRank().name()+"_"+card.getSuit().name())
            .collect(Collectors.toList());
        
        response.pot = PotResponse.fromEntity(gameState.getPot());
        
        return response;
    } 
        
	public String getCurrentPhase() {
		return currentPhase;
	}
	public void setCurrentPhase(String currentPhase) {
		this.currentPhase = currentPhase;
	}
	public List<PlayerResponse> getPlayers() {
		return players;
	}
	public void setPlayers(List<PlayerResponse> players) {
		this.players = players;
	}
	public List<String> getBoard() {
		return board;
	}
	public void setBoard(List<String> board) {
		this.board = board;
	}
	public PotResponse getPot() {
		return pot;
	}
	public void setPot(PotResponse pot) {
		this.pot = pot;
	}
	public String getCurrentPlayer() {
		return currentPlayer;
	}
	public void setCurrentPlayer(String currentPlayer) {
		this.currentPlayer = currentPlayer;
	}
	public int getCurrentHighestBet() {
		return currentHighestBet;
	}
	public void setCurrentHighestBet(int currentHighestBet) {
		this.currentHighestBet = currentHighestBet;
	}
	public int getSmallBlindAmount() {
		return smallBlindAmount;
	}
	public void setSmallBlindAmount(int smallBlindAmount) {
		this.smallBlindAmount = smallBlindAmount;
	}
	public int getBigBlindAmount() {
		return bigBlindAmount;
	}
	public void setBigBlindAmount(int bigBlindAmount) {
		this.bigBlindAmount = bigBlindAmount;
	}
	public int getButtonIndex() {
		return buttonIndex;
	}
	public void setButtonIndex(int buttonIndex) {
		this.buttonIndex = buttonIndex;
	}
	public boolean isBettingRoundComplete() {
		return bettingRoundComplete;
	}
	public void setBettingRoundComplete(boolean bettingRoundComplete) {
		this.bettingRoundComplete = bettingRoundComplete;
	}
    
    
}