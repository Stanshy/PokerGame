package com.chris.poker.domain;

public enum PlayerAction {
	NONE,     // 還沒行動
    FOLD,     // 棄牌
    CHECK,    // 過牌
    CALL,     // 跟注
    BET,      // 下注
    RAISE,    // 加注  
    ALL_IN    // 全押
}
