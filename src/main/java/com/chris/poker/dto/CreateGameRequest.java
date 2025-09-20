package com.chris.poker.dto;

import java.util.List;

public class CreateGameRequest {
    private List<PlayerDto> players;
    private int smallBlind;
    private int bigBlind;
    
    
    
    public List<PlayerDto> getPlayers() {
		return players;
	}



	public void setPlayers(List<PlayerDto> players) {
		this.players = players;
	}



	public int getSmallBlind() {
		return smallBlind;
	}



	public void setSmallBlind(int smallBlind) {
		this.smallBlind = smallBlind;
	}



	public int getBigBlind() {
		return bigBlind;
	}



	public void setBigBlind(int bigBlind) {
		this.bigBlind = bigBlind;
	}



	public static class PlayerDto {
        private String name;
        private int chips;
        
        
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getChips() {
			return chips;
		}
		public void setChips(int chips) {
			this.chips = chips;
		}
        
        
    }
}