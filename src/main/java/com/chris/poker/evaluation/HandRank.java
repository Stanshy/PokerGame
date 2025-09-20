package com.chris.poker.evaluation;

import java.util.List;
import java.util.Objects;

import com.chris.poker.card.Rank;


public class HandRank implements Comparable<HandRank> {
	private final HandType handType;
	private final List<Rank> kickers; 

	public HandRank(HandType handType, List<Rank> kickers) {
	    this.handType = Objects.requireNonNull(handType);
	    this.kickers = kickers != null ? List.copyOf(kickers) : List.of();
	}

	@Override
	public int compareTo(HandRank other) {
		int typeComparison = this.handType.compareTo(other.handType);
	    if (typeComparison != 0) {
	        return typeComparison;
	    }
	    
	    if (this.kickers.size() != other.kickers.size()) {
	        throw new IllegalStateException("Same hand type must have same number of kickers");
	    }
	    
	    for (int i = 0; i < this.kickers.size(); i++) {
	        int comparison = this.kickers.get(i).compareTo(other.kickers.get(i));
	        if (comparison != 0) {
	            return comparison;
	        }
	    }
	    return 0;
	}
	
	
	public HandType getHandType() {
	    return handType;
	}

	public List<Rank> getKickers() {
	    return kickers; 
	}
	
	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if(obj == null || getClass() != obj.getClass()) return false;
		HandRank handRank = (HandRank) obj;
		return handType == handRank.handType && Objects.equals(kickers, handRank.kickers);
			   
	}
	
	@Override
	public int hashCode() {
		return Objects.hash(handType,kickers);
	}
	
	@Override
	public String toString() {
	    return handType + " with kickers: " + kickers;
	}
}
