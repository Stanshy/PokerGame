package com.chris.poker.domain;


public enum GamePhase {
	PREFLOP, FLOP, TURN, RIVER, SHOWDOWN;
	
	
	public GamePhase getNext() {
		 switch (this) {
		 case PREFLOP:return FLOP;
		 case FLOP:return TURN;
		 case TURN:return RIVER;
		 case RIVER:return SHOWDOWN;
		 default: return null;
		 }
	}
}