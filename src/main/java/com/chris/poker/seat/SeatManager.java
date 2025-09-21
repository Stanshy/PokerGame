package com.chris.poker.seat;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

import org.springframework.stereotype.Component;

import com.chris.poker.domain.Player;

@Component 
public class SeatManager {
	
	private static final int MAX_SEATS = 8;
	
	private final Map<Integer,SeatInfo> seats = new ConcurrentHashMap<>();
	
	//加入玩家
	public SeatInfo joinSeat(int seatNumber,String playerName,int chips) {
		
		if(seats.containsKey(seatNumber)){
			throw new IllegalArgumentException("座位被占用");
		}
		
		boolean nameExist = seats.values().stream().anyMatch(s->s.getPlayerName().equals(playerName));
		if(nameExist) {
			throw new IllegalArgumentException("玩家名稱不可重覆");
		}
		if(chips <=0) {
			throw new IllegalArgumentException("籌碼必須大於0");
		}
		
		SeatInfo seatInfo = new SeatInfo(playerName, chips);
		seats.put(seatNumber, seatInfo);
		
		return seatInfo;
	}
	
	//將加入座位玩家轉為列表用於創建遊戲
	public List<Player> creatPlayersList(){
		return seats.entrySet().stream().sorted(Map.Entry.comparingByKey())
				.map(e->new Player(e.getValue().getPlayerName(),e.getValue().getChips()))
				.collect(Collectors.toList());
	}
	
	//離座(遊戲開始前)
	public void leaveSeat(int seatNum) {
		seats.remove(seatNum);
	}
	
	//獲取座位狀態
	public Map<Integer,SeatInfo> getAllSeats(){
		return new HashMap<>(seats);
	}
	
	
	//清空所有座位
	public void clear() {
		seats.clear();
	}
	
	//特定座位資訊
	public SeatInfo getSeatInfo(int seatNum) {
		return seats.get(seatNum);
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
}
