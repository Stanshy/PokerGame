package com.chris.poker.domain;


import java.util.Objects;


public class Action {
	private final PlayerAction type;
	private final int amount;

	private Action(PlayerAction type, int amount) {
		this.type = type;
		this.amount = amount;

	}

	private static boolean requiresAmount(PlayerAction type) {
		return type == PlayerAction.BET || type == PlayerAction.RAISE || type == PlayerAction.CALL
				|| type == PlayerAction.ALL_IN;
	}

	public static Action fold() {
		return new Action(PlayerAction.FOLD, 0);
	}

	public static Action check() {
		return new Action(PlayerAction.CHECK, 0);
	}

	public static Action call(int amount) {
		return new Action(PlayerAction.CALL, amount);
	}

	public static Action bet(int amount) {
		return new Action(PlayerAction.BET, amount);
	}

	public static Action raise(int amount) {
		return new Action(PlayerAction.RAISE, amount);
	}

	public static Action allIn(int amount) {
		return new Action(PlayerAction.ALL_IN, amount);
	}
	
	public PlayerAction getType() { 
        return type; 
    }
    
    public int getAmount() { 
        return amount; 
    }
    
    public boolean requiresAmount() {
        return requiresAmount(this.type);
    }
    
    public boolean isFold() {
        return type == PlayerAction.FOLD;
    }
    
    public boolean isCheck() {
        return type == PlayerAction.CHECK;
    }
    
    public boolean isCall() {
        return type == PlayerAction.CALL;
    }
    
    public boolean isBet() {
        return type == PlayerAction.BET;
    }
    
    public boolean isRaise() {
        return type == PlayerAction.RAISE;
    }
    
    public boolean isAllIn() {
        return type == PlayerAction.ALL_IN;
    }
    
    public boolean isAggressive() {
        return type == PlayerAction.BET || type == PlayerAction.RAISE || type == PlayerAction.ALL_IN;
    }
    
    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || getClass() != obj.getClass()) return false;
        Action action = (Action) obj;
        return amount == action.amount && type == action.type;
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(type, amount);
    }
    
    @Override
    public String toString() {
        if (requiresAmount()) {
            return type + "(" + amount + ")";
        } else {
            return type.toString();
        }
    }
}
