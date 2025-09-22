package com.chris.poker.dto;

import java.util.Map;

import com.chris.poker.dto.game.GameStateResponse;

public class WebSocketMessage {
    private String type; 
    private Object data;  
    private Long timestamp;
    
    public WebSocketMessage() {
        this.timestamp = System.currentTimeMillis();
    }
    
    public WebSocketMessage(String type, Object data) {
        this.type = type;
        this.data = data;
        this.timestamp = System.currentTimeMillis();
    }
    
   
    public static WebSocketMessage gameStateUpdate(GameStateResponse gameState) {
        return new WebSocketMessage("GAME_STATE_UPDATE", gameState);
    }
    
    public static WebSocketMessage playerAction(String playerName, String action) {
        return new WebSocketMessage("PLAYER_ACTION", 
            Map.of("player", playerName, "action", action));
    }
    
    public static WebSocketMessage phaseChange(String newPhase) {
        return new WebSocketMessage("PHASE_CHANGE", 
            Map.of("phase", newPhase));
    }
    
   
    public String getType() { return type; }
    public void setType(String type) { this.type = type; }
    
    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }
    
    public Long getTimestamp() { return timestamp; }
    public void setTimestamp(Long timestamp) { this.timestamp = timestamp; }
}