package com.chris.poker.card;

import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public class Board {
	private final List<Card> cards;
	
	
	public Board (List<Card> cards) {
		Objects.requireNonNull(cards);
		
		if (cards.size()>5) {
			throw new IllegalArgumentException("Board cannot have more than 5 cards");
		}
		
		validateNoDuplicates(cards);
		this.cards =List.copyOf(cards);
	}
	
	public Board() {
		this(List.of());
	}
	
	
	public void validateNoDuplicates(List<Card> cards) {
		Set<Card> cardSet = new HashSet<>(cards);
		if(cardSet.size() != cards.size()) {
			throw new IllegalArgumentException("Board cannot contain duplicate cards");
		}
	}
	
	public List<Card> getCards() {
	    return List.copyOf(cards);  
	}

	public int size() {
	    return cards.size();
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false;
		Board board = (Board) obj;
		
		return Objects.equals(cards, board.cards);
	}

	@Override
	public int hashCode() {
	    return Objects.hash(cards);
	}

	@Override
	public String toString() {
	    return cards.toString();  
	}
}	
