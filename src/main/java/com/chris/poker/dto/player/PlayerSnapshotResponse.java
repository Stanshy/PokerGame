package com.chris.poker.dto.player;

import java.util.List;
import java.util.stream.Collectors;

import com.chris.poker.domain.PlayerSnapshot;

public class PlayerSnapshotResponse {
	private String name;
	private List<String> hand;
	private int chips;
	private String lastAction;
	
	public static PlayerSnapshotResponse from (PlayerSnapshot snapshot) {
		PlayerSnapshotResponse dto = new PlayerSnapshotResponse();
		dto.name=snapshot.getName();
		dto.hand=snapshot.getHand().stream().map(c->c.getRank().name()+"_"+c.getSuit().name()).collect(Collectors.toList());
		dto.chips=snapshot.getChips();
		dto.lastAction=snapshot.getLastAction().name();
		
		return dto;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<String> getHand() {
		return hand;
	}

	public void setHand(List<String> hand) {
		this.hand = hand;
	}

	public int getChips() {
		return chips;
	}

	public void setChips(int chips) {
		this.chips = chips;
	}

	public String getLastAction() {
		return lastAction;
	}

	public void setLastAction(String lastAction) {
		this.lastAction = lastAction;
	}
	
	
}
