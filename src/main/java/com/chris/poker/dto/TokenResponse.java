package com.chris.poker.dto;

import java.util.Map;

public class TokenResponse {
	
	private Map<String,String> playerToken;
	
	public TokenResponse(Map<String, String> playerTokens) {
		this.playerToken=playerTokens;
	}
	public Map<String, String> getPlayerTokens() {
        return playerToken;
    }
    
    public void setPlayerTokens(Map<String, String> playerTokens) {
        this.playerToken = playerTokens;
    }
}
