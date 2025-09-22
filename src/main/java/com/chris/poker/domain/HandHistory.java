package com.chris.poker.domain;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import com.chris.poker.card.Card;

public class HandHistory {
	private int handNumber;
	private List<PlayerSnapshot> players;
	private List<Card> board;
	private int potAmount;
	private List<WinnerInfo> winners;
	private LocalDateTime timestamp;
	
	
	
	
	
	public HandHistory(int handNumber, List<Player> players, List<Card> board, int potAmount,
			List<WinnerInfo> winners) {
		super();
		this.handNumber = handNumber;
		this.players = players.stream().map(PlayerSnapshot::from).collect(Collectors.toList());
		this.board = new ArrayList<>(board);
		this.potAmount = potAmount;
		this.winners = new ArrayList<>(winners);
		this.timestamp = LocalDateTime.now();
	}
	
	
	public int getHandNumber() {
		return handNumber;
	}


	public void setHandNumber(int handNumber) {
		this.handNumber = handNumber;
	}


	public List<PlayerSnapshot> getPlayers() {
		return players;
	}


	public void setPlayers(List<PlayerSnapshot> players) {
		this.players = players;
	}


	public List<Card> getBoard() {
		return board;
	}


	public void setBoard(List<Card> board) {
		this.board = board;
	}


	public int getPotAmount() {
		return potAmount;
	}


	public void setPotAmount(int potAmount) {
		this.potAmount = potAmount;
	}


	public List<WinnerInfo> getWinners() {
		return winners;
	}


	public void setWinners(List<WinnerInfo> winners) {
		this.winners = winners;
	}


	public LocalDateTime getTimestamp() {
		return timestamp;
	}


	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}






	
}
