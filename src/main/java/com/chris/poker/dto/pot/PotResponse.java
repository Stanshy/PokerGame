package com.chris.poker.dto.pot;

import java.util.List;
import java.util.stream.Collectors;

import com.chris.poker.domain.Player;
import com.chris.poker.domain.Pot;


public class PotResponse {
    private int total;
    private List<SidePotResponse> sidePots;
    
    
    public static PotResponse fromEntity(Pot pot) {
        PotResponse response = new PotResponse();
        
        response.total = pot.getTotalAmount();
        response.sidePots = pot.getSidePots().stream()
            .map(SidePotResponse::fromEntity)
            .collect(Collectors.toList());
        
        return response;
    }
    
    public int getTotal() {
		return total;
	}




	public void setTotal(int total) {
		this.total = total;
	}




	public List<SidePotResponse> getSidePots() {
		return sidePots;
	}




	public void setSidePots(List<SidePotResponse> sidePots) {
		this.sidePots = sidePots;
	}




	public static class SidePotResponse {
        private int amount;
        private List<String> eligiblePlayers;
        
        public static SidePotResponse fromEntity(Pot.SidePot sidePot) {
            SidePotResponse response = new SidePotResponse();
            
            response.amount = sidePot.getAmount();
            response.eligiblePlayers = sidePot.getEligiblePlayers().stream()
                .map(Player::getName)
                .collect(Collectors.toList());
            
            return response;
        }

		public int getAmount() {
			return amount;
		}

		public void setAmount(int amount) {
			this.amount = amount;
		}

		public List<String> getEligiblePlayers() {
			return eligiblePlayers;
		}

		public void setEligiblePlayers(List<String> eligiblePlayers) {
			this.eligiblePlayers = eligiblePlayers;
		}
        
        

    }
}