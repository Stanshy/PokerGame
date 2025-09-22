package com.chris.poker.dto.seat;

import com.chris.poker.seat.SeatInfo;
import java.util.Map;

public class SeatsStateResponse {
    private Map<Integer, SeatInfo> seats;
    private int playerCount;
    private boolean canStartGame;
    
    public SeatsStateResponse(Map<Integer, SeatInfo> seats, int playerCount, boolean canStartGame) {
        this.seats = seats;
        this.playerCount = playerCount;
        this.canStartGame = canStartGame;
    }
    
    public Map<Integer, SeatInfo> getSeats() { return seats; }
    public void setSeats(Map<Integer, SeatInfo> seats) { this.seats = seats; }
    
    public int getPlayerCount() { return playerCount; }
    public void setPlayerCount(int playerCount) { this.playerCount = playerCount; }
    
    public boolean isCanStartGame() { return canStartGame; }
    public void setCanStartGame(boolean canStartGame) { this.canStartGame = canStartGame; }
}