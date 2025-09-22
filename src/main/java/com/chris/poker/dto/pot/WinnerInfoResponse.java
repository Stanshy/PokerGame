package com.chris.poker.dto.pot;

import com.chris.poker.domain.WinnerInfo;

public class WinnerInfoResponse {
	private String playerName;
	private int amountWon;
	
	public static WinnerInfoResponse from (WinnerInfo info) {
		WinnerInfoResponse dto = new WinnerInfoResponse();
		dto.playerName=info.getPlayName();
		dto.amountWon=info.getAmountWon();
		return dto;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayName(String playerName) {
		this.playerName = playerName;
	}

	public int getAmountWon() {
		return amountWon;
	}

	public void setAmountWon(int amountWon) {
		this.amountWon = amountWon;
	}
	
	
}
