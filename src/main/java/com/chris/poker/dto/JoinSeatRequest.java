package com.chris.poker.dto;

public class JoinSeatRequest {
    private int seatNumber;
    private String playerName;
    private int chips;
    
    public int getSeatNumber() { return seatNumber; }
    public void setSeatNumber(int seatNumber) { this.seatNumber = seatNumber; }
    
    public String getPlayerName() { return playerName; }
    public void setPlayerName(String playerName) { this.playerName = playerName; }
    
    public int getChips() { return chips; }
    public void setChips(int chips) { this.chips = chips; }
}