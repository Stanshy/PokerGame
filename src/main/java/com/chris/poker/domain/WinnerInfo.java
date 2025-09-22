package com.chris.poker.domain;

public  class WinnerInfo{
	private String playerName;
	private int amountWon;
	
	public WinnerInfo (String playerName,int amountWon) {
		this.playerName =playerName;
		this.amountWon=amountWon;
	}

	public String getPlayName() {
		return playerName;
	}

	public void setPlayName(String playName) {
		this.playerName = playName;
	}

	public int getAmountWon() {
		return amountWon;
	}

	public void setAmountWon(int amountWon) {
		this.amountWon = amountWon;
	}

	
	

}	