package com.chris.poker.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;


class BettingRoundTest {

	@Test
	void testSetupPreflop() {
	    System.out.println("=== 測試 setupPreflop ===");
	    
	    // 建立測試玩家
	    Player playerA = new Player("Alice", 1000);
	    Player playerB = new Player("Bob", 1000);
	    Player playerC = new Player("Charlie", 1000);
	    
	    // 設定位置
	    playerA.setSmallBlind(true);
	    playerB.setBigBlind(true);
	    // playerC 是 UTG
	    
	    List<Player> players = Arrays.asList(playerA, playerB, playerC);
	    
	    System.out.println("建立前狀態:");
	    for (Player p : players) {
	        System.out.println(p.getName() + " - 籌碼: " + p.getChips() + 
	                         ", 下注: " + p.getCurrentBet() + 
	                         ", 小盲: " + p.isSmallBlind() + 
	                         ", 大盲: " + p.isBigBlind());
	    }
	    
	    // 建立翻牌前下注輪
	    BettingRound round = new BettingRound(players, 50, 100, true);
	    
	    System.out.println("\n建立後狀態:");
	    for (Player p : players) {
	        System.out.println(p.getName() + " - 籌碼: " + p.getChips() + 
	                         ", 下注: " + p.getCurrentBet() + 
	                         ", 狀態: " + p.getStatus() + 
	                         ", 可動作: " + p.canAct());
	    }
	    
	    System.out.println("\n下注輪狀態:");
	    System.out.println("當前最高下注: " + round.getCurrentHighestBet());
	    System.out.println("當前玩家: " + round.getCurrentPlayer().getName());
	    System.out.println("是否結束: " + round.isRoundComplete());
	    
	    // 驗證
	    assertEquals(50, playerA.getCurrentBet()); // 小盲下注50
	    assertEquals(100, playerB.getCurrentBet()); // 大盲下注100
	    assertEquals(0, playerC.getCurrentBet()); // UTG 還沒動作
	    assertEquals(100, round.getCurrentHighestBet()); // 最高下注應該是大盲金額
	    assertEquals("Charlie", round.getCurrentPlayer().getName()); // 當前玩家應該是UTG
	}
	
	
//	@Test
	void testExecuteAction() {
	    System.out.println("=== 測試 executeAction ===");
	    
	    // 建立測試玩家
	    Player playerA = new Player("Alice", 1000);
	    Player playerB = new Player("Bob", 1000);
	    Player playerC = new Player("Charlie", 1000);
	    
	    playerA.setSmallBlind(true);
	    playerB.setBigBlind(true);
	    
	    List<Player> players = Arrays.asList(playerA, playerB, playerC);
	    BettingRound round = new BettingRound(players, 50, 100, true);
	    
	    System.out.println("初始狀態 - 當前玩家: " + round.getCurrentPlayer().getName());
	    
	    // Charlie (UTG) 跟注
	    Action callAction = Action.call(100);
	    boolean success = round.executeAction(playerC, callAction);
	    
	    System.out.println("\nCharlie 跟注後:");
	    System.out.println("執行成功: " + success);
	    System.out.println("Charlie 下注: " + playerC.getCurrentBet());
	    System.out.println("Charlie 籌碼: " + playerC.getChips());
	    System.out.println("當前玩家: " + round.getCurrentPlayer().getName());
	    System.out.println("是否結束: " + round.isRoundComplete());
	    
	    // Alice (小盲) 棄牌
	    Action foldAction = Action.fold();
	    boolean success2 = round.executeAction(playerA, foldAction);
	    
	    System.out.println("\nAlice 棄牌後:");
	    System.out.println("執行成功: " + success2);
	    System.out.println("Alice 狀態: " + playerA.getStatus());
	    System.out.println("當前玩家: " + round.getCurrentPlayer().getName());
	    System.out.println("是否結束: " + round.isRoundComplete());
	    
	    assertTrue(success);
	    assertTrue(success2);
	    assertEquals(100, playerC.getCurrentBet());
	    assertEquals("Bob", round.getCurrentPlayer().getName());
	}
	
//	@Test
	void testIsRoundCompleteLogic() {
	    System.out.println("=== 測試 isRoundComplete 邏輯 ===");
	    
	    Player playerA = new Player("Alice", 1000);
	    Player playerB = new Player("Bob", 1000);
	    Player playerC = new Player("Charlie", 1000);
	    
	    playerA.setSmallBlind(true);
	    playerB.setBigBlind(true);
	    
	    List<Player> players = Arrays.asList(playerA, playerB, playerC);
	    BettingRound round = new BettingRound(players, 50, 100, true);
	    
	    // Charlie 跟注，Alice 棄牌
	    round.executeAction(playerC, Action.call(100));
	    round.executeAction(playerA, Action.fold());
	    
	    System.out.println("當前狀態:");
	    for (Player p : Arrays.asList(playerA, playerB, playerC)) {
	        System.out.println(p.getName() + " - 在手牌中: " + p.isInHand() + 
	                         ", 可動作: " + p.canAct() + 
	                         ", 下注: " + p.getCurrentBet());
	    }
	    
	    System.out.println("當前玩家: " + round.getCurrentPlayer().getName());
	    System.out.println("當前最高下注: " + round.getCurrentHighestBet());
	    System.out.println("是否結束: " + round.isRoundComplete());
	    
	    // Bob 應該還能 check 或 raise
	    System.out.println("Bob 是否可以 check: " + (round.getCurrentPlayer().getCurrentBet() == round.getCurrentHighestBet()));
	}
	
//	@Test
	void testBigBlindOptions() {
	    System.out.println("=== 測試大盲選擇權 ===");
	    
	    Player playerA = new Player("Alice", 1000);
	    Player playerB = new Player("Bob", 1000);  
	    Player playerC = new Player("Charlie", 1000);
	    
	    playerA.setSmallBlind(true);
	    playerB.setBigBlind(true);
	    
	    List<Player> players = Arrays.asList(playerA, playerB, playerC);
	    BettingRound round = new BettingRound(players, 50, 100, true);
	    
	    // Charlie 跟注，Alice 棄牌
	    round.executeAction(playerC, Action.call(100));
	    round.executeAction(playerA, Action.fold());
	    
	    System.out.println("Alice canAct: " + playerA.canAct());
	    System.out.println("Alice status: " + playerA.getStatus());
	    
	    System.out.println("輪到 Bob，他可以:");
	    System.out.println("當前玩家: " + round.getCurrentPlayer().getName());
	    System.out.println("Bob 當前下注: " + playerB.getCurrentBet());
	    System.out.println("最高下注: " + round.getCurrentHighestBet());
	    
	    // Bob 選擇 check
	    boolean checkSuccess = round.executeAction(playerB, Action.check());
	    
	    System.out.println("\nBob check 後:");
	    System.out.println("執行成功: " + checkSuccess);
	    System.out.println("是否結束: " + round.isRoundComplete());
	    System.out.println("當前玩家: " + (round.getCurrentPlayer() != null ? round.getCurrentPlayer().getName() : "無"));
	    
	    assertTrue(checkSuccess);
	    assertTrue(round.isRoundComplete()); // Bob check 後應該結束
	}
	
//	 @Test 
	    void testSetupPostflop() {
	        System.out.println("=== 測試翻牌後設置 ===");
	        
	        Player playerA = new Player("Alice", 1000);
	        Player playerB = new Player("Bob", 1000);
	        Player playerC = new Player("Charlie", 1000);
	        
	        playerA.setSmallBlind(true);
	        playerB.setBigBlind(true);
	        
	        List<Player> players = Arrays.asList(playerA, playerB, playerC);
	        
	        // 翻牌後下注輪
	        BettingRound round = new BettingRound(players, 50, 100, false);
	        
	        System.out.println("翻牌後狀態:");
	        for (Player p : players) {
	            System.out.println(p.getName() + " - 籌碼: " + p.getChips() + 
	                             ", 下注: " + p.getCurrentBet() + 
	                             ", 可動作: " + p.canAct());
	        }
	        
	        System.out.println("當前最高下注: " + round.getCurrentHighestBet());
	        System.out.println("當前玩家: " + (round.getCurrentPlayer() != null ? round.getCurrentPlayer().getName() : "無"));
	        
	        // 驗證
	        assertEquals(0, round.getCurrentHighestBet()); // 翻牌後最高下注應該是0
	        assertEquals("Alice", round.getCurrentPlayer().getName()); // 小盲先動作
	        assertEquals(0, playerA.getCurrentBet()); // 所有人下注都重置為0
	        assertEquals(0, playerB.getCurrentBet());
	        assertEquals(0, playerC.getCurrentBet());
	    }

//	    @Test
	    void testRaiseScenario() {
	        System.out.println("=== 測試加注場景 ===");
	        
	        Player playerA = new Player("Alice", 1000);
	        Player playerB = new Player("Bob", 1000);
	        Player playerC = new Player("Charlie", 1000);
	        
	        playerA.setSmallBlind(true);
	        playerB.setBigBlind(true);
	        
	        List<Player> players = Arrays.asList(playerA, playerB, playerC);
	        BettingRound round = new BettingRound(players, 50, 100, true);
	        
	        System.out.println("初始 - 當前玩家: " + round.getCurrentPlayer().getName());
	        
	        // Charlie 加注到 200
	        boolean raiseSuccess = round.executeAction(playerC, Action.raise(200));
	        System.out.println("Charlie 加注後:");
	        System.out.println("執行成功: " + raiseSuccess);
	        System.out.println("Charlie 下注: " + playerC.getCurrentBet());
	        System.out.println("當前最高下注: " + round.getCurrentHighestBet());
	        System.out.println("當前玩家: " + (round.getCurrentPlayer() != null ? round.getCurrentPlayer().getName() : "無"));
	        System.out.println("是否結束: " + round.isRoundComplete());
	        
	        // Alice 跟注
	        boolean callSuccess = round.executeAction(playerA, Action.call(200));
	        System.out.println("\nAlice 跟注後:");
	        System.out.println("執行成功: " + callSuccess);
	        System.out.println("Alice 下注: " + playerA.getCurrentBet());
	        System.out.println("當前玩家: " + (round.getCurrentPlayer() != null ? round.getCurrentPlayer().getName() : "無"));
	        
	        // Bob 棄牌
	        boolean foldSuccess = round.executeAction(playerB, Action.fold());
	        System.out.println("\nBob 棄牌後:");
	        System.out.println("執行成功: " + foldSuccess);
	        System.out.println("Bob 狀態: " + playerB.getStatus());
	        System.out.println("是否結束: " + round.isRoundComplete());
	        
	        assertTrue(raiseSuccess);
	        assertTrue(callSuccess);
	        assertTrue(foldSuccess);
	        assertEquals(200, round.getCurrentHighestBet());
	        assertTrue(round.isRoundComplete()); // 只剩兩人且下注相同，應該結束
	    }
	    
//	    @Test
	    void testAllInScenario() {
	        System.out.println("=== 測試全押場景 ===");
	        
	        // 建立籌碼較少的玩家
	        Player playerA = new Player("Alice", 150);  // 小盲，籌碼150
	        Player playerB = new Player("Bob", 300);    // 大盲，籌碼300
	        Player playerC = new Player("Charlie", 1000);
	        
	        playerA.setSmallBlind(true);
	        playerB.setBigBlind(true);
	        
	        List<Player> players = Arrays.asList(playerA, playerB, playerC);
	        BettingRound round = new BettingRound(players, 50, 100, true);
	        
	        System.out.println("設置後狀態:");
	        System.out.println("Alice 籌碼: " + playerA.getChips() + ", 下注: " + playerA.getCurrentBet() + ", 狀態: " + playerA.getStatus());
	        System.out.println("Bob 籌碼: " + playerB.getChips() + ", 下注: " + playerB.getCurrentBet() + ", 狀態: " + playerB.getStatus());
	        
	        // Charlie 加注到 500
	        System.out.println("\nCharlie 加注到 500:");
	        boolean raiseSuccess = round.executeAction(playerC, Action.raise(500));
	        System.out.println("執行成功: " + raiseSuccess);
	        System.out.println("當前最高下注: " + round.getCurrentHighestBet());
	        
	        // Alice 全押（她只有100籌碼剩餘）
	        System.out.println("\nAlice 全押:");
	        boolean allinSuccess = round.executeAction(playerA, Action.allIn());
	        System.out.println("執行成功: " + allinSuccess);
	        System.out.println("Alice 籌碼: " + playerA.getChips());
	        System.out.println("Alice 下注: " + playerA.getCurrentBet());
	        System.out.println("Alice 狀態: " + playerA.getStatus());
	        System.out.println("當前最高下注: " + round.getCurrentHighestBet());
	        
	        // Bob 也全押
	        System.out.println("\nBob 全押:");
	        boolean bobAllinSuccess = round.executeAction(playerB, Action.allIn());
	        System.out.println("執行成功: " + bobAllinSuccess);
	        System.out.println("Bob 籌碼: " + playerB.getChips());
	        System.out.println("Bob 下注: " + playerB.getCurrentBet());
	        System.out.println("Bob 狀態: " + playerB.getStatus());
	        System.out.println("當前最高下注: " + round.getCurrentHighestBet());
	        System.out.println("是否結束: " + round.isRoundComplete());
	        
	        assertTrue(raiseSuccess);
	        assertTrue(allinSuccess);
	        assertTrue(bobAllinSuccess);
	        assertTrue(round.isRoundComplete()); // 沒有可動作玩家，應該結束
	    }
	    
//	    @Test
	    void testPotIntegration() {
	        System.out.println("=== 測試與 Pot 的整合 ===");
	        
	        Player playerA = new Player("Alice", 1000);
	        Player playerB = new Player("Bob", 1000);
	        Player playerC = new Player("Charlie", 1000);
	        
	        playerA.setSmallBlind(true);
	        playerB.setBigBlind(true);
	        
	        List<Player> players = Arrays.asList(playerA, playerB, playerC);
	        BettingRound round = new BettingRound(players, 50, 100, true);
	        
	        // 簡單場景：所有人跟注
	        round.executeAction(playerC, Action.call(100)); // Charlie 跟注
	        round.executeAction(playerA, Action.call(100)); // Alice 跟注（補足到100）
	        round.executeAction(playerB, Action.check());   // Bob 過牌
	        
	        System.out.println("Alice hasActed: " + playerA.hasActed() + ", lastAction: " + playerA.getLastAction());
	        System.out.println("Bob hasActed: " + playerB.hasActed() + ", lastAction: " + playerB.getLastAction());  
	        System.out.println("Charlie hasActed: " + playerC.hasActed() + ", lastAction: " + playerC.getLastAction());
	        
	        
	        System.out.println("下注結束後各玩家狀態:");
	        for (Player p : players) {
	            System.out.println(p.getName() + " - 下注: " + p.getCurrentBet());
	        }
	        assertTrue(round.isRoundComplete());
	        
	        // 創建底池並收集籌碼
	        Pot pot = new Pot();
	        pot.collectBets(players);
	        
	        System.out.println("\n底池收集後:");
	        System.out.println("底池總額: " + pot.getTotalAmount());
	        System.out.println("邊池數量: " + pot.getSidePotCount());
	        System.out.println("底池詳情: " + pot.toString());
	        
	        System.out.println("\n收集後各玩家狀態:");
	        for (Player p : players) {
	            System.out.println(p.getName() + " - 籌碼: " + p.getChips() + ", 當前下注: " + p.getCurrentBet());
	        }
	        
	        assertEquals(300, pot.getTotalAmount()); // 三人各100
	        assertEquals(1, pot.getSidePotCount()); // 應該只有一個主池
	    }
	    
//	    @Test
	    void testComplexAllInPotIntegration() {
	        System.out.println("=== 測試複雜全押與底池整合 ===");
	        
	        Player playerA = new Player("Alice", 50);   // 小盲
	        Player playerB = new Player("Bob", 150);    // 大盲  
	        Player playerC = new Player("Charlie", 1000);
	        
	        playerA.setSmallBlind(true);
	        playerB.setBigBlind(true);
	        
	        List<Player> players = Arrays.asList(playerA, playerB, playerC);
	        BettingRound round = new BettingRound(players, 50, 100, true);
	        
	        System.out.println("初始狀態:");
	        for (Player p : players) {
	            System.out.println(p.getName() + " - 籌碼: " + p.getChips() + ", 下注: " + p.getCurrentBet());
	        }
	        
	        // Charlie 跟注 100
	        System.out.println("\n1. Charlie 跟注 100:");
	        round.executeAction(playerC, Action.call(100));
	        System.out.println("當前玩家: " + (round.getCurrentPlayer() != null ? round.getCurrentPlayer().getName() : "無"));
	        System.out.println("當前最高下注: " + round.getCurrentHighestBet());
	        
	        // Alice 全押（只能全押到 50，因為已經下了 50）
	        System.out.println("\n2. Alice 全押:");
	        round.executeAction(playerA, Action.allIn());
	        System.out.println("Alice 下注: " + playerA.getCurrentBet());
	        System.out.println("當前玩家: " + (round.getCurrentPlayer() != null ? round.getCurrentPlayer().getName() : "無"));
	        System.out.println("當前最高下注: " + round.getCurrentHighestBet());
	        
	        // Bob 全押（已下注 100，再全押剩餘 50）
	        System.out.println("\n3. Bob 全押:");
	        round.executeAction(playerB, Action.allIn());
	        System.out.println("Bob 下注: " + playerB.getCurrentBet());
	        System.out.println("當前玩家: " + (round.getCurrentPlayer() != null ? round.getCurrentPlayer().getName() : "無"));
	        System.out.println("當前最高下注: " + round.getCurrentHighestBet());
	        System.out.println("是否結束: " + round.isRoundComplete());
	        
	        // 關鍵：此時應該輪回到 Charlie，讓他決定是否跟注到 150
	        if (!round.isRoundComplete() && round.getCurrentPlayer() != null) {
	            System.out.println("\n4. 輪回到 Charlie，他需要決定:");
	            System.out.println("Charlie 當前下注: " + playerC.getCurrentBet());
	            System.out.println("需要跟注到: " + round.getCurrentHighestBet());
	            
	            // Charlie 跟注到 150
	            System.out.println("\n5. Charlie 跟注到 150:");
	            round.executeAction(playerC, Action.call(150));
	            System.out.println("Charlie 下注: " + playerC.getCurrentBet());
	            System.out.println("是否結束: " + round.isRoundComplete());
	        }
	        
	        System.out.println("\n最終狀態:");
	        for (Player p : players) {
	            System.out.println(p.getName() + " - 籌碼: " + p.getChips() + 
	                             ", 下注: " + p.getCurrentBet() + 
	                             ", 狀態: " + p.getStatus());
	        }
	        
	        // 收集到底池
	        Pot pot = new Pot();
	        pot.collectBets(players);
	        
	        System.out.println("\n底池分析:");
	        System.out.println("總額: " + pot.getTotalAmount());
	        System.out.println("邊池數量: " + pot.getSidePotCount());
	        
	        List<Pot.SidePot> sidePots = pot.getSidePots();
	        for (int i = 0; i < sidePots.size(); i++) {
	            Pot.SidePot sidePot = sidePots.get(i);
	            System.out.println("池子 " + i + ": 金額=" + sidePot.getAmount() + 
	                             ", 參與者數量=" + sidePot.getEligiblePlayers().size());
	            for (Player p : sidePot.getEligiblePlayers()) {
	                System.out.println("  - " + p.getName());
	            }
	        }
	    }
	    
//	    @Test
	    void testEdgeCases() {
	        System.out.println("=== 測試邊緣情況 ===");
	        
	        // 測試只剩一個玩家的情況
	        Player playerA = new Player("Alice", 1000);
	        Player playerB = new Player("Bob", 1000);
	        
	        playerA.setSmallBlind(true);
	        playerB.setBigBlind(true);
	        
	        List<Player> players = Arrays.asList(playerA, playerB);
	        BettingRound round = new BettingRound(players, 50, 100, true);
	        
	        System.out.println("兩人對戰，Alice 棄牌:");
	        boolean foldSuccess = round.executeAction(playerA, Action.fold());
	        
	        System.out.println("執行成功: " + foldSuccess);
	        System.out.println("是否結束: " + round.isRoundComplete());
	        System.out.println("剩餘玩家: " + round.getPlayersInHand().size());
	        
	        for (Player p : round.getPlayersInHand()) {
	            System.out.println("剩餘玩家: " + p.getName());
	        }
	        
	        assertTrue(foldSuccess);
	        assertTrue(round.isRoundComplete());
	        assertEquals(1, round.getPlayersInHand().size());
	    }
	    
}
