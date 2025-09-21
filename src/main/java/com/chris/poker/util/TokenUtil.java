package com.chris.poker.util;

import com.chris.poker.security.PlayerSession;
import com.chris.poker.service.TokenService;

public class TokenUtil {

	public static String extractToken(String authHeader) {
		if (authHeader == null || authHeader.isEmpty()) {
			throw new IllegalArgumentException("缺少Authorization header");
		}

		if (!authHeader.startsWith("Bearer ")) {
			throw new IllegalArgumentException("Authorization header格式錯誤，應為: Bearer {token}");
		}
		return authHeader.substring(7);
	}

	public static String getPlayerName(String authHeader, TokenService tokenService) {
		String token = extractToken(authHeader);
		PlayerSession session = tokenService.validateToken(token);
		return session.getPlayerName();
	}
}
