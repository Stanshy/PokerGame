package com.chris.poker.dto.player;

import java.util.List;
import java.util.stream.Collectors;

import com.chris.poker.card.Hand;


public class PlayerHandResponse {
    private List<String> cards;
    
    
    public static PlayerHandResponse fromEntity(Hand hand) {
        PlayerHandResponse response = new PlayerHandResponse();
        
        response.cards = hand.getCards().stream()
            .map(card -> card.getRank().name()+"_"+card.getSuit().name())
            .collect(Collectors.toList());
        
        return response;
    }

	public List<String> getCards() {
		return cards;
	}

	public void setCards(List<String> cards) {
		this.cards = cards;
	}
    
    
}