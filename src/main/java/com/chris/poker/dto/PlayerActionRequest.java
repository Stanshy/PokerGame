package com.chris.poker.dto;

public class PlayerActionRequest {
    private String action;
    private Integer amount;
    
    
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public Integer getAmount() {
		return amount;
	}
	public void setAmount(Integer amount) {
		this.amount = amount;
	}
    
    
}