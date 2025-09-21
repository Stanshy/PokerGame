package com.chris.poker.security;

import java.time.LocalDateTime;

public class PlayerSession {
	private String playerName;
	private String token;
	private LocalDateTime createdAt;
	private LocalDateTime lastActivity;
	
	public PlayerSession(String playerName, String token) {
        this.playerName = playerName;
        this.token = token;
        this.createdAt = LocalDateTime.now();
        this.lastActivity = LocalDateTime.now();
    }
	
	public boolean isExpired() {
		return LocalDateTime.now().minusMinutes(30).isAfter(lastActivity);//保存30分鐘 超過就過期
	}
	
	public void updateLastActivity() {
		this.lastActivity = LocalDateTime.now();
	}

	public String getPlayerName() {
		return playerName;
	}

	public String getToken() {
		return token;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public LocalDateTime getLastActivity() {
		return lastActivity;
	}
	
	
}
