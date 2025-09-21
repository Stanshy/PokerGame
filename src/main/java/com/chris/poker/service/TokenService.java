package com.chris.poker.service;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.stereotype.Service;

import com.chris.poker.security.PlayerSession;

@Service
public class TokenService {

	private final Map<String, PlayerSession> activeSession = new ConcurrentHashMap<>();// 執行緒安全

	// 創建玩家token
	public String createPlayerToken(String playerName) {
		String token = UUID.randomUUID().toString(); // 8-4-4-4-12 隨機產生的識別碼
		PlayerSession session = new PlayerSession(playerName, token);
		activeSession.put(token, session);
		return token;
	}

	// 驗證玩家token
	public PlayerSession validateToken(String token) {

		if (token == null || token.isEmpty()) {
			throw new IllegalArgumentException("token不可為空");
		}
		PlayerSession session = activeSession.get(token);

		if (session == null) {
			throw new IllegalArgumentException("無效token");
		}

		if (session.isExpired()) {
			throw new IllegalArgumentException("token已過期");
		}

		session.updateLastActivity();
		return session;
	}
	//刪除token
	public void removeToken(String token) {
		activeSession.remove(token);
	}
	//清除所有
	public void clearAllTokens() {
		activeSession.clear();
	}
}
