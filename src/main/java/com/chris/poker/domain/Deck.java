package com.chris.poker.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.chris.poker.card.*;

public class Deck {

	private List<Card> cards;

	public Deck() {
		reset();
	}

	public void reset() {
		this.cards = creatCards();
		shuffle();
	}

	public List<Card> creatCards() {
		var cards = new ArrayList<Card>();
		for (Suit suit : Suit.values()) {
			for (Rank rank : Rank.values()) {
				cards.add(new Card(suit, rank));
			}
		}
		return cards;
	}

	public void shuffle() {
		Collections.shuffle(cards); // 洗牌
	}

	// 發單張牌
	public Card dealCard() {
		return cards.remove(cards.size() - 1); // 發最後一張牌
	}

	// 發多張公共牌(flop)
	public List<Card> dealCards(int num) {
		var cards = new ArrayList<Card>();
		for (int i = 0; i < num; i++) {
			cards.add(dealCard());
		}
		return cards;
	}

	// 發玩家手牌
	public void dealPlayerHand(Player player) {
		Card card1 = dealCard();
		Card card2 = dealCard();

		player.setHand(new Hand(card1, card2));
	}

	// 發玩家手牌
	public void dealPlayersCard(List<Player> players) {
		for (Player player : players) {
			if (player.isInHand()) {
				dealPlayerHand(player);
			}
		}
	}

	// 燒牌
	public void burnCard() {
		cards.remove(cards.size() - 1);
	}

	// 發翻牌
	public Board dealFlop() {
		burnCard();
		List<Card> flopCards = dealCards(3);
		return new Board(flopCards);
	}

	// 發轉牌
	public Board dealTurn(Board board) {
		burnCard();
		var newCards = new ArrayList<Card>(board.getCards());
		newCards.add(dealCard());
		return new Board(newCards);
	}

	// 發河牌
	public Board dealRiver(Board board) {
		burnCard();
		var newCards = new ArrayList<Card>(board.getCards());
		newCards.add(dealCard());
		return new Board(newCards);
	}

	@Override
	public String toString() {
		return "Deck{" + "remainingCards=" + cards.size() + '}';
	}

}
