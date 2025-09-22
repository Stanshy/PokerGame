package com.chris.poker.dto.game;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

import com.chris.poker.domain.HandHistory;
import com.chris.poker.dto.player.PlayerSnapshotResponse;
import com.chris.poker.dto.pot.WinnerInfoResponse;

public class HandHistoryResponse {
	private int handNumber;
	private List<PlayerSnapshotResponse> players;
	private List<String> board;
	private int potAmount;
	private List<WinnerInfoResponse> winners;
	private LocalDateTime timestamp;
	
	public static HandHistoryResponse from(HandHistory history) {
		HandHistoryResponse dto= new HandHistoryResponse();
		dto.handNumber = history.getHandNumber();
		dto.players = history.getPlayers().stream().map(PlayerSnapshotResponse::from).collect(Collectors.toList());
		dto.board = history.getBoard().stream().map(c->c.getRank().name()+"_"+c.getSuit().name()).collect(Collectors.toList());
		dto.potAmount = history.getPotAmount();
		dto.winners = history.getWinners().stream().map(WinnerInfoResponse::from).collect(Collectors.toList());
		dto.timestamp=history.getTimestamp();
		return dto;
	}

	public int getHandNumber() {
		return handNumber;
	}

	public void setHandNumber(int handNumber) {
		this.handNumber = handNumber;
	}

	public List<PlayerSnapshotResponse> getPlayers() {
		return players;
	}

	public void setPlayers(List<PlayerSnapshotResponse> players) {
		this.players = players;
	}

	public List<String> getBoard() {
		return board;
	}

	public void setBoard(List<String> board) {
		this.board = board;
	}

	public int getPotAmount() {
		return potAmount;
	}

	public void setPotAmount(int potAmount) {
		this.potAmount = potAmount;
	}

	public List<WinnerInfoResponse> getWinners() {
		return winners;
	}

	public void setWinners(List<WinnerInfoResponse> winners) {
		this.winners = winners;
	}

	public LocalDateTime getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(LocalDateTime timestamp) {
		this.timestamp = timestamp;
	}
	
	
}
