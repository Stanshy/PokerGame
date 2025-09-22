package com.chris.poker.domain;

import java.util.List;

import com.chris.poker.card.Card;

public class PlayerSnapshot{
	private String name;
	private List<Card> hand;
	private int chips;
	private PlayerAction lastAction;
	
	public static PlayerSnapshot from(Player player) {
		PlayerSnapshot snapshot =new PlayerSnapshot();
		snapshot.name=player.getName();
		snapshot.hand=player.getHand().getCards();
		snapshot.chips=player.getChips();
		snapshot.lastAction=player.getLastAction();
				
		return snapshot;
		
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Card> getHand() {
		return hand;
	}

	public void setHand(List<Card> hand) {
		this.hand = hand;
	}

	public int getChips() {
		return chips;
	}

	public void setChips(int chips) {
		this.chips = chips;
	}

	public PlayerAction getLastAction() {
		return lastAction;
	}

	public void setLastAction(PlayerAction lastAction) {
		this.lastAction = lastAction;
	}
	
	
	
	
	
}