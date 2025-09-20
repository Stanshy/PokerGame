package com.chris.poker.card;

import java.util.Objects;

public class Card implements Comparable<Card> {
	private final Suit suit;
	private final Rank rank;
	
	
	public Card (Suit suit, Rank rank) {
		this.suit= Objects.requireNonNull(suit);
		this.rank= Objects.requireNonNull(rank);
	}
	
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (obj == null || getClass() != obj.getClass()) return false;
		Card card = (Card) obj;
		return suit == card.suit && rank == card.rank;
	}
	
	@Override
	public String toString() {
		return rank + " of " + suit;
	}
	
	@Override
	public int compareTo(Card other) {
		return this.rank.compareTo(other.rank);
	}
	
	@Override
	public int hashCode() {
	    return Objects.hash(suit, rank);
	}


	public Suit getSuit() {
		return suit;
	}


	public Rank getRank() {
		return rank;
	}
	
	
	
}
