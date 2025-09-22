package com.chris.poker.domain;

import java.util.*;
import java.util.stream.Collectors;





/**
 * 管理德州撲克的底池系統 支援主池和邊池的分配
 */
public class Pot {

	
	public final List<SidePot> pots;
	public int totalAmount;
	
	public Pot() {
		this.pots = new ArrayList<>();
		this.totalAmount = 0;
	}
	
	//加進底池
	public void addtoPot(int amount , List<Player>eligiblePlayers) {
		if (eligiblePlayers.isEmpty()) {
			throw new IllegalArgumentException("Must have at least one eligible player");
		}

		totalAmount += amount;
		//如果沒有底池則建立主池
		if(pots.isEmpty()) {
			pots.add(new SidePot(amount, new ArrayList<Player>(eligiblePlayers)));
			return;
		}
		SidePot lastPot = pots.get(pots.size()-1);
		//如果參與玩家一樣就把下注加到同一個底池 ，如果不同則是出現邊池情況則新建邊池
		if(hasSameEligiblePlayers(lastPot.getEligiblePlayers(),eligiblePlayers)) {
			lastPot.addAmount(amount);
		}else {
			pots.add(new SidePot(amount, new ArrayList<Player>(eligiblePlayers)));
		}
	}
	
	//計算主池及邊池邏輯
	public void collectBets(List<Player> players) {
		//有下注的玩家 且按照下注金額小到大
		List<Player> bettingPlayers = players.stream()
				.filter(p->p.getCurrentBet()>0)
				.sorted((p1,p2)->Integer.compare(p1.getCurrentBet(),p2.getCurrentBet()))
				.collect(Collectors.toList());
		if (bettingPlayers.isEmpty()) {
			return;
		}
		int preBetLevel=0;
		//每當有人下注金額大於先前下注等級 就會更新底池  
		for(int i = 0 ; i<bettingPlayers.size();i++) {
			Player p = bettingPlayers.get(i);
			int currentBet= p.getCurrentBet();
			int betDifferent = currentBet-preBetLevel;
			if(betDifferent>0) {
				int amount = betDifferent*(bettingPlayers.size()-i);
				List<Player> eligiblePlayers = new ArrayList<Player>();
				for (int j = i; j < bettingPlayers.size(); j++) {
					eligiblePlayers.add(bettingPlayers.get(j));
				}
				addtoPot(amount, eligiblePlayers);
			}
			preBetLevel=currentBet;
		}
		for (Player player : players) {
			player.resetForNewBettingRound();
		}
	}
	
	//分配獲勝獎金
	public List<WinnerInfo>  distributePots(List<List<Player>> winners) {
		List<WinnerInfo> winnerInfo = new ArrayList<>();
		
		for(int i = 0 ; i<pots.size(); i++) {
			SidePot pot = pots.get(i);
			List<Player> potWinners = winners.get(i);
			List<Player> eligibleWinners = potWinners.stream()
					.filter(pot::isEligible)
					.collect(Collectors.toList());
			if(eligibleWinners.isEmpty()) {
				continue;
			}
			int amountPerWinner = pot.getAmount() / eligibleWinners.size();
			int remainder = pot.getAmount() % eligibleWinners.size(); //線上沒有最小籌碼面值 分到1為止;
			for(int j =0 ;j<eligibleWinners.size();j++) {
				Player winner = eligibleWinners.get(j);
				int winAmount = amountPerWinner;
				if(j<remainder) {
					winAmount+=1;
				}
				winner.winChips(winAmount);
				winnerInfo.add(new WinnerInfo(winner.getName(), winAmount));
			}
			pot.clear();
		}
		return winnerInfo;
	}
	
	//一個贏家
	public int awardToWinner(Player winner) {
		int totalWinnings = totalAmount;
		winner.winChips(totalWinnings);
		clear();
		return totalWinnings;
	}
	//多個贏家分一個底池
	public int splitPot(List<Player> winners) {
		int amountPerWinner = totalAmount / winners.size();
		int remainder = totalAmount % winners.size();
		for (int i = 0; i < winners.size(); i++) {
			Player winner = winners.get(i);
			int winAmount = amountPerWinner;

			// 把餘數給前面的獲勝者
			if (i < remainder) {
				winAmount += 1;
			}

			winner.winChips(winAmount);
		}

		clear();
		return amountPerWinner;
	}
	
	
	//判斷是否是一樣的玩家參與
	private boolean hasSameEligiblePlayers(List<Player>list1,List<Player>list2) {
		if (list1.size() != list2.size()) {
			return false;
		}
		return new HashSet<>(list1).equals(new HashSet<>(list2)); //不管順序

	}
	
	//清空底池
	public void clear() {
		pots.clear();
		totalAmount = 0;
	}
	
	
	
	
	
	
	public int getTotalAmount() {
		return totalAmount;
	}

	public int getSidePotCount() {
		return pots.size();
	}

	public List<SidePot> getSidePots() {
		return new ArrayList<>(pots);
	}

	public boolean isEmpty() {
		return totalAmount == 0;
	}

	@Override
	public String toString() {
		if (pots.isEmpty()) {
			return "Empty Pot";
		}

		StringBuilder sb = new StringBuilder();
		sb.append("Total Pot: ").append(totalAmount);

		if (pots.size() == 1) {
			sb.append(" (Main Pot: ").append(pots.get(0).getAmount()).append(")");
		} else {
			sb.append(" (Main Pot: ").append(pots.get(0).getAmount());
			for (int i = 1; i < pots.size(); i++) {
				sb.append(", Side Pot ").append(i).append(": ").append(pots.get(i).getAmount());
			}
			sb.append(")");
		}

		return sb.toString();
	}
	
	
	
	
	
	
	
	
	
	
	//內部類別 只有此類別用到
	public static class SidePot {
		private int amount;
		private final List<Player> eligiblePlayers;

		public SidePot(int amount, List<Player> eligiblePlayers) {
			this.amount = amount;
			this.eligiblePlayers = new ArrayList<>(eligiblePlayers);
		}

		public void addAmount(int additionalAmount) {
			this.amount += additionalAmount;
		}

		public boolean isEligible(Player player) {
			return eligiblePlayers.contains(player);
		}

		public void clear() {
			this.amount = 0;
		}

		// Getters
		public int getAmount() {
			return amount;
		}

		public List<Player> getEligiblePlayers() {
			return new ArrayList<>(eligiblePlayers);
		}

		@Override
		public String toString() {
			return "SidePot{amount=" + amount + ", players=" + eligiblePlayers.size() + "}";
		}
	}
}