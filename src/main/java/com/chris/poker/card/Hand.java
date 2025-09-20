package com.chris.poker.card;

import java.util.List;
import java.util.Objects;

public class Hand {
	private final Card card1;
	private final Card card2;
	
	public Hand (Card card1, Card card2) {
		this.card1= Objects.requireNonNull(card1);
		this.card2= Objects.requireNonNull(card2);
	if (card1.equals(card2)) {
		throw new IllegalArgumentException("Hand cannot contain duplicate cards");
	}
	}
	
	public List<Card> getCards(){
		return List.of(card1,card2);
	}
	
	public boolean contains(Card card) {
		return card1.equals(card) || card2.equals(card);
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false;
		Hand other =(Hand) obj;
		
		return  (card1.equals(other.card1) && card2.equals(other.card2)) ||
				(card1.equals(other.card2) && card2.equals(other.card1));
	}
	
	@Override
	public String toString() {
		return card1.toString() +" , "+ card2.toString();
	}
	
	@Override
	public int hashCode() {
		return card1.hashCode() + card2.hashCode();
		
	}
}
