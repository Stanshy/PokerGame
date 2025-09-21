	package com.chris.poker.domain;
	
	import java.util.*;
	import java.util.stream.Collectors;
	
	import com.chris.poker.card.*;
import com.chris.poker.evaluation.HandEvaluator;
import com.chris.poker.evaluation.HandRank;
	
	public class GameState {
	//	private final int maxSeats;
	//	private final Player[] seats;
		private List<Player> players;	
		private GamePhase currentPhase;
		
		
		private Deck deck;
	    private Board board;
	    private Pot pot;
	    private BettingRound currentBettingRound;
	    private int smallBlindAmount;
	    private int bigBlindAmount;
	    private int buttonIndex;  
	    
	    public GameState(List<Player> players , int smallblind,int bigBlind) {
	    	this.players = new ArrayList<>(players);
	    	this.smallBlindAmount=smallblind;
	    	this.bigBlindAmount=bigBlind;
	    	this.deck=new Deck();
	    	this.board =new Board();
	    	this.pot=new Pot();
	    	this.buttonIndex=  0;
	    	this.currentPhase =GamePhase.PREFLOP;
	    }
	    
	    
	    public void startNewHand() {
	    	resetFromNewHand();
	    	moveButton();
	    	resetPlayerSeat();
	    	deck.dealPlayersCard(players);
	    	currentBettingRound = new BettingRound(players, smallBlindAmount, bigBlindAmount, true);
	        currentPhase = GamePhase.PREFLOP;
	    }
	    
	    //重製遊戲及玩家狀態
	    public void resetFromNewHand() {
	    	currentBettingRound = null;
	    	board =new Board();
	    	pot.clear();
	    	deck.reset();
	    	
	    	for(Player p: players) {
	    		p.resetForNewHand();
	    	}
	    }
	    //移動莊位至下一位玩家
	    public void moveButton() {
	    	buttonIndex =getNextPosition(buttonIndex);
	    }
	    
	    //輪替位置往下一個
	    public int getNextPosition(int startSeat) {
	    	int nextSeat =(startSeat+1)%players.size();
	    	return nextSeat;
	    }
	    //調整玩家位置狀態做輪替
	    public void resetPlayerSeat() {
	    	for(Player p : players) {
	    		p.setButton(false);
	    		p.setBigBlind(false);
	    		p.setSmallBlind(false);
	    	}
	    	int buttonSeat = buttonIndex;
	    	int sbSeat = getNextPosition(buttonSeat);
	    	int bbSeat = getNextPosition(sbSeat);
	    	
	    	players.get(buttonSeat).setButton(true);
	    	players.get(sbSeat).setSmallBlind(true);
	    	players.get(bbSeat).setBigBlind(true);
	    }
	    
	    //階段控制
	    public void nextPhase() {
	    	collectBetsToPot();
	    	
	    	 GamePhase nextPhase = currentPhase.getNext();
	    	 currentPhase = nextPhase;
	    	 if(nextPhase == GamePhase.SHOWDOWN) {
	    		 handleShowdown();
	    	 }else {
	    		 dealCommunityCards();
	    		 startBettingRound();
	    	 }
	    }
	    
	    /*
	     * 下注輪管理
	     */
	    private void startBettingRound() {
	    	List<Player> playersInHand = getPlayersInHand();
	    	boolean isPreflop = (currentPhase == GamePhase.PREFLOP);
	    	currentBettingRound = new BettingRound(playersInHand, smallBlindAmount, bigBlindAmount, isPreflop);
	    }
	    
	    public boolean isCurrentBettingRoundComplete() {
	        return currentBettingRound != null && currentBettingRound.isRoundComplete();
	    }
	
	    public boolean canNextPhase() {
	        return isCurrentBettingRoundComplete();
	    }
	    
	    /*
	     * 玩家管理
	     */
	    public Player getCurrentPlayer() {
	        if (currentBettingRound == null) return null;
	        return currentBettingRound.getCurrentPlayer();
	    }
	    
	    public List<Player> getPlayersInHand(){
	    	return players.stream().filter(Player::isInHand).collect(Collectors.toList());
	    }
	    
	    
	    //發牌
	    private void dealCommunityCards() {
	    	switch (currentPhase) {
	    	case FLOP:
	    		board = deck.dealFlop();
	    		break;
	    	case TURN:
	    		board = deck.dealTurn(board);
	    		break;
	    	case RIVER:
	    		board = deck.dealRiver(board);
	    		break;
	    	}
	    }
	    
	    
	    //收集下注
	    private void collectBetsToPot() {
	    	if(currentBettingRound != null) {
	    		pot.collectBets(currentBettingRound.getPlayers());
	    	}
	    }
	    
	    
	    
	    //接口層
	    public boolean executePlayerAction(Player player, Action action) {
	        if (currentBettingRound == null) return false;
	        return currentBettingRound.executeAction(player, action);
	    }
	    
	    
	    //處理攤牌
	    private void handleShowdown() {
	    	List<Player> playerInHnad =getPlayersInHand();
	    	if(playerInHnad.size() ==1) {
	    		Player winner = playerInHnad.get(0);
	    		pot.awardToWinner(winner);
	    	}else {
	    		distributePot();
	    	}
	    }
	    
	    //獲取攤牌玩家手牌資訊
	    public Map<String, List<Card>> getShowdownHands(){
	    	List<Player> playerInHnad =getPlayersInHand();
	    	Map<String, List<Card>> showdownHands = new HashMap<>();
	    	
	    	if(currentPhase == GamePhase.SHOWDOWN) {
	    		for(Player p :playerInHnad ) {
	    			showdownHands.put(p.getName(), p.getHand().getCards());
	    		}
	    	}
	    	return showdownHands;
	    }
	    
	    
	    //計算排型大小
	    private Map<Player,HandRank> compareHands(List<Player> players) {
	    	Map<Player,HandRank> playersRank = new HashMap<Player, HandRank>();
	    	for(Player p : players) {
	    		List<Card> cards = new ArrayList<Card>();
	    		cards.addAll(p.getHand().getCards());
	    		cards.addAll(board.getCards());
	    		HandRank rank = HandEvaluator.evaluateHand(cards);
	    		playersRank.put(p, rank);
	    	}
	    	return playersRank;
	    }
	    //處理底池(含邊池)
	    private  void distributePot() {
	    	List<List<Player>>  potWinners = new ArrayList<>();
	    	for(int i = 0;i<pot.getSidePotCount();i++) {
	    		Pot.SidePot sidePot=pot.getSidePots().get(i);
	    		List<Player> eligiblePlayers = sidePot.getEligiblePlayers();
	    		List<Player> winners = findWinnersFromEligible(eligiblePlayers);
	    		potWinners.add(winners);
	    	}
	    	pot.distributePots(potWinners);
	    }
	    
	    //找出贏家
	    private List<Player> findWinnersFromEligible(List<Player> eligiblePlayers) {
	    	Map<Player, HandRank> eligiblePlayerRanks = compareHands(eligiblePlayers);
	    	List<Player> winners = new ArrayList<>();
	        HandRank bestRank = null;
	        for(Map.Entry<Player, HandRank> entry : eligiblePlayerRanks.entrySet()) {
	        	HandRank rank = entry.getValue();
	        	if(bestRank==null || rank.compareTo(bestRank)>0) {
	        		bestRank=rank;
	        		winners.clear();
	        		winners.add(entry.getKey());
	        	}else if (rank.compareTo(bestRank) == 0) {
	        		winners.add(entry.getKey());
	        	}
	        }
	        return winners;
	    }
	    
	    //遊戲循環控制
	    public boolean canStartNextHand() {
	        // 如果是SHOWDOWN階段，可以開始新手牌
	        if (currentPhase == GamePhase.SHOWDOWN) {
	            return true;
	        }
	        
	        // 如果是PREFLOP但還沒發牌，也可以開始（初始狀態）
	        if (currentPhase == GamePhase.PREFLOP && players.stream().allMatch(p -> p.getHand() == null)) {
	            return true;
	        }
	        
	        return false;
	    }
	    public void startNextHand() {
	    	if(canStartNextHand()) {
	    		startNewHand();
	    	}
	    }
	    
	    public GamePhase getCurrentPhase() { return currentPhase; }
	    public Board getBoard() { return board; }
	    public Pot getPot() { return pot; }
	    public int getSmallBlindAmount() { return smallBlindAmount; }
	    public int getBigBlindAmount() { return bigBlindAmount; }
	    public int getButtonIndex() { return buttonIndex; }
	    public BettingRound getCurrentBettingRound() { return currentBettingRound; }
	    
	    public int getCurrentHighestBet() {
	        if (currentBettingRound == null) return 0;
	        return currentBettingRound.getCurrentHighestBet();
	    }
	    
	}