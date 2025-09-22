package com.chris.poker.dto.seat;

import com.chris.poker.seat.SeatInfo;

public class JoinSeatResponse {
    private SeatInfo seatInfo;
    private String token;
    
    public JoinSeatResponse(SeatInfo seatInfo, String token) {
        this.seatInfo = seatInfo;
        this.token = token;
    }
    
    public SeatInfo getSeatInfo() { return seatInfo; }
    public void setSeatInfo(SeatInfo seatInfo) { this.seatInfo = seatInfo; }
    
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
}