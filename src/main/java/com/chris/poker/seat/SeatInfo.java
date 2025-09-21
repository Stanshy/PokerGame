package com.chris.poker.seat;

public class SeatInfo {
    private String playerName;   
    private int chips;           
    private long joinedAt;       
    
    public SeatInfo( String playerName, int chips) {
        this.playerName = playerName;
        this.chips = chips;
        this.joinedAt = System.currentTimeMillis();
    }


	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public int getChips() {
		return chips;
	}

	public void setChips(int chips) {
		this.chips = chips;
	}

	public long getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(long joinedAt) {
		this.joinedAt = joinedAt;
	}
    
   
    
}