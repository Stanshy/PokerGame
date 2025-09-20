package com.chris.poker.evaluation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

import com.chris.poker.card.Card;
import com.chris.poker.card.Rank;
import com.chris.poker.card.Suit;


public class HandEvaluator {
	
	public static HandRank evaluateHand(List<Card> cards) {
	    if (cards.size() < 5 || cards.size() > 7) {
	        throw new IllegalArgumentException("Must have 5-7 cards to evaluate");
	    }
	    
	    // 按牌型強度從高到低檢查
	    if (isRoyalFlush(cards)) {
	        return new HandRank(HandType.ROYAL_FLUSH, List.of());
	    }
	    
	    HandRank straightFlush = checkStraightFlush(cards);
	    if (straightFlush != null) return straightFlush;
	    
	    HandRank fourOfAKind = checkFourOfAKind(cards);
	    if (fourOfAKind != null) return fourOfAKind;
	    
	    HandRank fullHouse = checkFullHouse(cards);
	    if (fullHouse != null) return fullHouse;
	    
	    HandRank flush = checkFlush(cards);
	    if (flush != null) return flush;
	    
	    HandRank straight = checkStraight(cards);
	    if (straight != null) return straight;
	    
	    HandRank threeOfAKind = checkThreeOfAKind(cards);
	    if (threeOfAKind != null) return threeOfAKind;
	    
	    HandRank twoPair = checkTwoPair(cards);
	    if (twoPair != null) return twoPair;
	    
	    HandRank pair = checkPair(cards);
	    if (pair != null) return pair;
	    
	    return checkHighCard(cards);
	}

	private static boolean isRoyalFlush(List<Card> cards) {
		Map<Suit, List<Card>> suitGroups = groupBySuit(cards);
		for (List<Card> suitCards : suitGroups.values()) {
			if (suitCards.size() >= 5) {
				Set<Rank> ranks = new HashSet<>();
				for (Card card : suitCards) {
					ranks.add(card.getRank());
				}
				return ranks.contains(Rank.TEN) && ranks.contains(Rank.JACK) && ranks.contains(Rank.QUEEN)
						&& ranks.contains(Rank.KING) && ranks.contains(Rank.ACE);
			}
		}
		return false;
	}

	private static HandRank checkStraightFlush(List<Card> cards) {
		Map<Suit, List<Card>> suitGroups = groupBySuit(cards);
		for (List<Card> suitCards : suitGroups.values()) {
			if (suitCards.size() >= 5) {
				List<Rank> ranks = new ArrayList<>();
				for (Card card : suitCards) {
					ranks.add(card.getRank());
				}
				Rank highestRank = isStraightSequence(ranks);
				if (highestRank != null) {
					if (!isWheelStraight(ranks)) {
						return new HandRank(HandType.STRAIGHT_FLUSH, List.of(highestRank));
					} else {

						return new HandRank(HandType.STRAIGHT_FLUSH, List.of(Rank.FIVE));
					}
				}
			}
		}
		return null;
	}

	private static HandRank checkFourOfAKind(List<Card> cards) {
		Map<Rank, Integer> rankCounts = getRankCounts(cards);
		for (Rank rank : rankCounts.keySet()) {
			if (rankCounts.get(rank) == 4) {
				Rank highestKicker = null;
				for (Rank otherRank : rankCounts.keySet()) {
					if (!otherRank.equals(rank)) {
						if (highestKicker == null || otherRank.compareTo(highestKicker) > 0) {
							highestKicker = otherRank;
						}
					}
				}
				return new HandRank(HandType.FOUR_OF_KIND, List.of(rank, highestKicker));
			}

		}
		return null;
	}

	private static HandRank checkFullHouse(List<Card> cards) {
		Map<Rank, Integer> rankCounts = getRankCounts(cards);
		Rank bestThreeOfAKind = null;
		for (Rank rank : rankCounts.keySet()) {
			if (rankCounts.get(rank) == 3) {
				if (bestThreeOfAKind == null || rank.compareTo(bestThreeOfAKind) > 0) {
					bestThreeOfAKind = rank;
				}
			}
		}
		Rank bestPair = null;
		for (Rank otherRank : rankCounts.keySet()) {
			if (!otherRank.equals(bestThreeOfAKind)) {
				if (rankCounts.get(otherRank) >= 2) {
					if (bestPair == null || otherRank.compareTo(bestPair) > 0) {
						bestPair = otherRank;
					}
				}
			}
		}
		if (bestThreeOfAKind != null && bestPair != null) {
			return new HandRank(HandType.FULL_HOUSE, List.of(bestThreeOfAKind, bestPair));
		}
		return null;
	}

	private static HandRank checkFlush(List<Card> cards) {
		Map<Suit, List<Card>> suitGroups = groupBySuit(cards);
		for (List<Card> suitCards : suitGroups.values()) {
			if (suitCards.size() >= 5) {
				suitCards.sort((card1, card2) -> card2.getRank().compareTo(card1.getRank()));
				List<Card> bestFiveCards = suitCards.subList(0, 5);

				List<Rank> kickers = new ArrayList<>();
				for (Card card : bestFiveCards) {
					kickers.add(card.getRank());
				}
				return new HandRank(HandType.FLUSH, kickers);
			}
		}
		return null;
	}

	private static HandRank checkStraight(List<Card> cards) {
		List<Rank> ranks = new ArrayList<>();
		for (Card card : cards) {
			ranks.add(card.getRank());
		}
		Rank highestKieker = isStraightSequence(ranks);
		if (highestKieker != null) {
			if (!isWheelStraight(ranks)) {
				return new HandRank(HandType.STRAIGHT, List.of(highestKieker));
			} else {
				return new HandRank(HandType.STRAIGHT, List.of(Rank.FIVE));
			}
		}
		return null;
	}

	private static HandRank checkThreeOfAKind(List<Card> cards) {
		Map<Rank, Integer> rankCounts = getRankCounts(cards);
		List<Rank> ranks = new ArrayList<>();
		Rank threeOfAKindRank = null;
		for (Rank rank : rankCounts.keySet()) {
			if (rankCounts.get(rank) == 3) {
				threeOfAKindRank = rank;
				break;
			}
			
		}
		if (threeOfAKindRank == null) return null;
		for(Rank otherRank : rankCounts.keySet()) {
			if(!otherRank.equals(threeOfAKindRank)) {
				if(rankCounts.get(otherRank) >= 2) {
					return null;
				}else {
					ranks.add(otherRank);
				}
			}
		}
		if (ranks.size() < 2) return null;
		
		ranks.sort((rank1, rank2) -> rank2.compareTo(rank1));
		List<Rank> kickers = new ArrayList<>();
		kickers.add(threeOfAKindRank);
		kickers.add(ranks.get(0));
		kickers.add(ranks.get(1));
		
		return new HandRank(HandType.THREE_OF_KIND, kickers);
		
	}

	private static HandRank checkTwoPair(List<Card> cards) {
		 Map<Rank, Integer> rankCounts = getRankCounts(cards);
		 List<Rank> pairs = new ArrayList<>();
		 List<Rank> kickers = new ArrayList<>();
		 List<Rank> remainingRanks  = new ArrayList<>();
		 
		 for(Rank rank : rankCounts.keySet()) {
			 if (rankCounts.get(rank) == 2 ) {
				 pairs.add(rank);
			 }
		 }
		 if (pairs.size()<2) return null;
		 pairs.sort((rank1,rank2) -> rank2.compareTo(rank1));
		 Rank biggerPair = pairs.get(0);
		 Rank smallerPair = pairs.get(1);
		 kickers.add(biggerPair);
		 kickers.add(smallerPair);
		 
		 for(Rank otherRank :  rankCounts.keySet()) {
			 if(otherRank != biggerPair && otherRank != smallerPair) {
				 remainingRanks.add(otherRank);
			 }
		 }
		 remainingRanks.sort((rank1,rank2) -> rank2.compareTo(rank1));
		 kickers.add(remainingRanks.get(0));
		 
		 return new HandRank(HandType.TWO_PAIR, kickers);
		 
	}

	private static HandRank checkPair(List<Card> cards) {
		Map<Rank, Integer> rankCounts = getRankCounts(cards);
		Rank pair = null;
		List<Rank> kickers = new ArrayList<>();
		List<Rank> remainingRanks  = new ArrayList<>();
		
		for(Rank rank:rankCounts.keySet()) {
			if(rankCounts.get(rank) ==2) {
				pair= rank;
				break;
			}
		}
		if (pair == null) return null; 
		
		for(Rank otherRank :rankCounts.keySet()) {
			if (!otherRank.equals(pair)) {
				if (rankCounts.get(otherRank)>= 2) {
					return null;
				}
				remainingRanks.add(otherRank);
			}
		}
		remainingRanks.sort((rank1,rank2) -> rank2.compareTo(rank1));
		kickers.add(pair);
		kickers.add(remainingRanks.get(0));
		kickers.add(remainingRanks.get(1));
		kickers.add(remainingRanks.get(2));
		
		
		return  new HandRank(HandType.PAIR, kickers);
	}

	private static HandRank checkHighCard(List<Card> cards) {
		Map<Rank, Integer> rankCounts = getRankCounts(cards);
		List<Rank> kickers = new ArrayList<>();
		
		for (Rank rank : rankCounts.keySet()) {
	        if (rankCounts.get(rank) >= 2) {
	            return null;  
	        }else {
	        	kickers.add(rank);
			}
	    }
		Map<Suit, List<Card>> suitGroups = groupBySuit(cards);
		for(List<Card> suitCards : suitGroups.values()) {
			if(suitCards.size()>=5 ) {
				return null;
			}
		}
		List<Rank> ranks = new ArrayList<>();
		for (Card card : cards) {
			ranks.add(card.getRank());
		}
		Rank highestKieker = isStraightSequence(ranks);
		if(highestKieker != null) {
			return null;
		}
		kickers.sort((rank1, rank2) -> rank2.compareTo(rank1));
		List<Rank> top5Kickers = kickers.subList(0, 5); 
		return new HandRank(HandType.HIGH_CARD, top5Kickers);
	}

	// 基礎分析方法
	// 計算牌點數量
	private static Map<Rank, Integer> getRankCounts(List<Card> cards) {
		Map<Rank, Integer> counts = new HashMap<>();

		for (Card card : cards) {
			Rank rank = card.getRank();
			counts.put(rank, counts.getOrDefault(rank, 0) + 1); // 檢查是否有這個key 沒有就回傳參數b(設置為0) 有就回傳這個key的value
		}

		return counts;
	}

	// 計算花色含點數 分組
	private static Map<Suit, List<Card>> groupBySuit(List<Card> cards) {
		Map<Suit, List<Card>> suitGroups = new HashMap<>();

		for (Card card : cards) {
			Suit suit = card.getSuit();

			if (!suitGroups.containsKey(suit)) {
				suitGroups.put(suit, new ArrayList<>());
			}
			suitGroups.get(suit).add(card);
		}
		return suitGroups;

	}


	// 特定功能方法
	private static Rank isStraightSequence(List<Rank> ranks) {
		if (ranks.size() < 5)
			return null;

		Set<Rank> uniqueRanks = new TreeSet<>(ranks);
		List<Rank> sortedRanks = new ArrayList<>(uniqueRanks);

		Rank bestHighCard = null;

		for (int i = 0; i <= sortedRanks.size() - 5; i++) {
			boolean isConsecutive = true;
			for (int j = 0; j < 4; j++) {
				if (sortedRanks.get(i + j + 1).ordinal() != sortedRanks.get(i + j).ordinal() + 1) {
					isConsecutive = false;
					break;
				}
			}
			if (isConsecutive) {
				Rank currentHighCard = sortedRanks.get(i + 4);
				if (bestHighCard == null || currentHighCard.compareTo(bestHighCard) > 0) {
					bestHighCard = currentHighCard;
				}
			}
		}
		return bestHighCard;
	}

	// 檢查是否是a-5的順子
	private static boolean isWheelStraight(List<Rank> ranks) {
		return ranks.contains(Rank.ACE) && ranks.contains(Rank.TWO) && ranks.contains(Rank.THREE)
				&& ranks.contains(Rank.FOUR) && ranks.contains(Rank.FIVE);
	}

}
