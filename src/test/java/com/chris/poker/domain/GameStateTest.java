package com.chris.poker.domain;

import static org.junit.jupiter.api.Assertions.*;

import java.util.Arrays;
import java.util.List;

import org.junit.jupiter.api.Test;

class GameStateTest {
	
//	@Test
	void testGameInitializationAndFirstHand() {
	    System.out.println("=== 測試遊戲初始化和第一手牌 ===");
	    
	    // 建立玩家
	    Player playerA = new Player("Alice", 1000);
	    Player playerB = new Player("Bob", 1000);
	    Player playerC = new Player("Charlie", 1000);
	    
	    System.out.println("建立玩家:");
	    System.out.println("Alice: " + playerA.getChips() + " 籌碼");
	    System.out.println("Bob: " + playerB.getChips() + " 籌碼");
	    System.out.println("Charlie: " + playerC.getChips() + " 籌碼");
	    
	    List<Player> players = Arrays.asList(playerA, playerB, playerC);
	    GameState game = new GameState(players, 50, 100);
	    
	    System.out.println("\n遊戲初始狀態:");
	    System.out.println("當前階段: " + game.getCurrentPhase());
	    System.out.println("按鈕位置: " + game.getButtonIndex());
	    System.out.println("小盲金額: " + game.getSmallBlindAmount());
	    System.out.println("大盲金額: " + game.getBigBlindAmount());
	    
	    // 開始第一手牌
	    game.startNewHand();
	    
	    System.out.println("\n第一手牌開始後:");
	    System.out.println("當前階段: " + game.getCurrentPhase());
	    System.out.println("按鈕位置: " + game.getButtonIndex());
	    
	    // 檢查位置設定
	    for (int i = 0; i < players.size(); i++) {
	        Player p = players.get(i);
	        System.out.println(p.getName() + " (位置" + i + "): " +
	                         "按鈕=" + p.isButton() + 
	                         ", 小盲=" + p.isSmallBlind() + 
	                         ", 大盲=" + p.isBigBlind() +
	                         ", 籌碼=" + p.getChips() +
	                         ", 下注=" + p.getCurrentBet());
	    }
	    
	    System.out.println("\n下注狀態:");
	    System.out.println("當前玩家: " + (game.getCurrentPlayer() != null ? game.getCurrentPlayer().getName() : "無"));
	    System.out.println("當前最高下注: " + game.getCurrentHighestBet());
	    System.out.println("底池總額: " + game.getPot().getTotalAmount());
	    
	    // 驗證
	    assertEquals(GamePhase.PREFLOP, game.getCurrentPhase());
	    assertNotNull(game.getCurrentPlayer());
	    assertTrue(game.getCurrentHighestBet() > 0); // 應該有大盲下注
	    
	    System.out.println("\n✓ 遊戲初始化測試完成");
	}
	
//	@Test
	void testPreflopBettingRound() {
	    System.out.println("=== 測試翻牌前下注輪 ===");
	    
	    Player playerA = new Player("Alice", 1000);
	    Player playerB = new Player("Bob", 1000);
	    Player playerC = new Player("Charlie", 1000);
	    List<Player> players = Arrays.asList(playerA, playerB, playerC);
	    
	    GameState game = new GameState(players, 50, 100);
	    game.startNewHand();
	    
	    System.out.println("翻牌前開始:");
	    System.out.println("當前玩家: " + game.getCurrentPlayer().getName());
	    System.out.println("需要跟注到: " + game.getCurrentHighestBet());
	    
	    // Bob (UTG) 跟注
	    System.out.println("\n1. Bob 跟注:");
	    boolean bobCall = game.executePlayerAction(playerB, Action.call(100));
	    System.out.println("執行成功: " + bobCall);
	    System.out.println("Bob 籌碼: " + playerB.getChips() + ", 下注: " + playerB.getCurrentBet());
	    System.out.println("當前玩家: " + (game.getCurrentPlayer() != null ? game.getCurrentPlayer().getName() : "無"));
	    System.out.println("是否可進入下階段: " + game.canNextPhase());
	    
	    // Charlie (小盲) 跟注
	    System.out.println("\n2. Charlie 跟注:");
	    boolean charlieCall = game.executePlayerAction(playerC, Action.call(100));
	    System.out.println("執行成功: " + charlieCall);
	    System.out.println("Charlie 籌碼: " + playerC.getChips() + ", 下注: " + playerC.getCurrentBet());
	    System.out.println("當前玩家: " + (game.getCurrentPlayer() != null ? game.getCurrentPlayer().getName() : "無"));
	    System.out.println("是否可進入下階段: " + game.canNextPhase());
	    
	    // Alice (大盲) 過牌
	    System.out.println("\n3. Alice (大盲) 過牌:");
	    boolean aliceCheck = game.executePlayerAction(playerA, Action.check());
	    System.out.println("執行成功: " + aliceCheck);
	    System.out.println("Alice 籌碼: " + playerA.getChips() + ", 下注: " + playerA.getCurrentBet());
	    System.out.println("當前玩家: " + (game.getCurrentPlayer() != null ? game.getCurrentPlayer().getName() : "無"));
	    System.out.println("是否可進入下階段: " + game.canNextPhase());
	    
	    System.out.println("\n翻牌前結束狀態:");
	    for (Player p : players) {
	        System.out.println(p.getName() + ": 籌碼=" + p.getChips() + ", 下注=" + p.getCurrentBet());
	    }
	    
	    // 驗證
	    assertTrue(bobCall);
	    assertTrue(charlieCall);
	    assertTrue(aliceCheck);
	    assertTrue(game.canNextPhase());
	    assertNull(game.getCurrentPlayer()); // 下注輪結束應該沒有當前玩家
	    
	    System.out.println("\n✓ 翻牌前下注輪測試完成");
	}
	
//	@Test
	void testFlopPhaseAndBetting() {
	    System.out.println("=== 測試翻牌階段和翻牌後下注 ===");
	    
	    Player playerA = new Player("Alice", 1000);
	    Player playerB = new Player("Bob", 1000);
	    Player playerC = new Player("Charlie", 1000);
	    List<Player> players = Arrays.asList(playerA, playerB, playerC);
	    
	    GameState game = new GameState(players, 50, 100);
	    game.startNewHand();
	    
	    // 完成翻牌前（快速完成）
	    game.executePlayerAction(playerB, Action.call(100));
	    game.executePlayerAction(playerC, Action.call(100));
	    game.executePlayerAction(playerA, Action.check());
	    
	    System.out.println("翻牌前結束，準備進入翻牌:");
	    System.out.println("可否進入下階段: " + game.canNextPhase());
	    System.out.println("當前階段: " + game.getCurrentPhase());
	    
	    // 進入翻牌階段
	    game.nextPhase();
	    
	    System.out.println("\n翻牌階段開始:");
	    System.out.println("當前階段: " + game.getCurrentPhase());
	    System.out.println("公共牌數量: " + game.getBoard().getCards().size());
	    System.out.println("底池總額: " + game.getPot().getTotalAmount());
	    System.out.println("當前最高下注: " + game.getCurrentHighestBet());
	    System.out.println("當前玩家: " + (game.getCurrentPlayer() != null ? game.getCurrentPlayer().getName() : "無"));
	    
	    // 檢查玩家下注狀態重置
	    System.out.println("\n玩家狀態 (翻牌後):");
	    for (Player p : players) {
	        System.out.println(p.getName() + ": 籌碼=" + p.getChips() + ", 當前下注=" + p.getCurrentBet());
	    }
	    
	    // 翻牌後下注：所有人過牌
	    System.out.println("\n翻牌後下注:");
	    
	    // Charlie (小盲) 過牌
	    System.out.println("1. Charlie 過牌:");
	    boolean charlieCheck = game.executePlayerAction(playerC, Action.check());
	    System.out.println("執行成功: " + charlieCheck);
	    System.out.println("當前玩家: " + game.getCurrentPlayer().getName());
	    
	    // Alice 過牌  
	    System.out.println("2. Alice 過牌:");
	    boolean aliceCheck = game.executePlayerAction(playerA, Action.check());
	    System.out.println("執行成功: " + aliceCheck);
	    System.out.println("當前玩家: " + game.getCurrentPlayer().getName());
	    
	    // Bob 過牌
	    System.out.println("3. Bob 過牌:");
	    boolean bobCheck = game.executePlayerAction(playerB, Action.check());
	    System.out.println("執行成功: " + bobCheck);
	    System.out.println("當前玩家: " + (game.getCurrentPlayer() != null ? game.getCurrentPlayer().getName() : "無"));
	    System.out.println("是否可進入下階段: " + game.canNextPhase());
	    
	    // 驗證
	    assertEquals(GamePhase.FLOP, game.getCurrentPhase());
	    assertEquals(3, game.getBoard().getCards().size()); // 翻牌應該有3張牌
	    assertEquals(300, game.getPot().getTotalAmount()); // 翻牌前的下注已收集
	    assertEquals(0, game.getCurrentHighestBet()); // 翻牌後重置為0
	    assertTrue(charlieCheck && aliceCheck && bobCheck);
	    assertTrue(game.canNextPhase());
	    
	    System.out.println("\n✓ 翻牌階段測試完成");
	}
	
//	@Test
	void testTurnRiverAndShowdown() {
	    System.out.println("=== 測試轉牌、河牌和攤牌 ===");
	    
	    Player playerA = new Player("Alice", 1000);
	    Player playerB = new Player("Bob", 1000);
	    Player playerC = new Player("Charlie", 1000);
	    List<Player> players = Arrays.asList(playerA, playerB, playerC);
	    
	    GameState game = new GameState(players, 50, 100);
	    game.startNewHand();
	    
	    // 快速完成翻牌前
	    game.executePlayerAction(playerB, Action.call(100));
	    game.executePlayerAction(playerC, Action.call(100));
	    game.executePlayerAction(playerA, Action.check());
	    
	    // 快速完成翻牌
	    game.nextPhase();
	    game.executePlayerAction(playerC, Action.check());
	    game.executePlayerAction(playerA, Action.check());
	    game.executePlayerAction(playerB, Action.check());
	    
	    System.out.println("翻牌完成，進入轉牌:");
	    System.out.println("當前階段: " + game.getCurrentPhase());
	    
	    // 進入轉牌
	    game.nextPhase();
	    System.out.println("\n轉牌階段:");
	    System.out.println("當前階段: " + game.getCurrentPhase());
	    System.out.println("公共牌數量: " + game.getBoard().getCards().size());
	    System.out.println("當前玩家: " + game.getCurrentPlayer().getName());
	    
	    // 轉牌全過牌
	    game.executePlayerAction(playerC, Action.check());
	    game.executePlayerAction(playerA, Action.check());
	    game.executePlayerAction(playerB, Action.check());
	    
	    System.out.println("轉牌完成，進入河牌:");
	    
	    // 進入河牌
	    game.nextPhase();
	    System.out.println("\n河牌階段:");
	    System.out.println("當前階段: " + game.getCurrentPhase());
	    System.out.println("公共牌數量: " + game.getBoard().getCards().size());
	    System.out.println("當前玩家: " + game.getCurrentPlayer().getName());
	    
	    // 河牌全過牌
	    game.executePlayerAction(playerC, Action.check());
	    game.executePlayerAction(playerA, Action.check());
	    game.executePlayerAction(playerB, Action.check());
	    
	    System.out.println("河牌完成，進入攤牌:");
	    System.out.println("是否可進入下階段: " + game.canNextPhase());
	    
	    // 進入攤牌
	    game.nextPhase();
	    System.out.println("\n攤牌階段:");
	    System.out.println("當前階段: " + game.getCurrentPhase());
	    System.out.println("當前玩家: " + (game.getCurrentPlayer() != null ? game.getCurrentPlayer().getName() : "無"));
	    System.out.println("底池總額: " + game.getPot().getTotalAmount());
	    
	    // 檢查玩家籌碼變化（攤牌後）
	    System.out.println("\n攤牌後玩家狀態:");
	    for (Player p : players) {
	        System.out.println(p.getName() + ": 籌碼=" + p.getChips());
	    }
	    
	    // 檢查是否可以開始下一手
	    System.out.println("\n遊戲循環:");
	    System.out.println("可以開始下一手: " + game.canStartNextHand());
	    
	    // 驗證
	    assertEquals(GamePhase.SHOWDOWN, game.getCurrentPhase());
	    assertEquals(5, game.getBoard().getCards().size()); // 應該有5張公共牌
	    assertTrue(game.canStartNextHand());
	    
	    System.out.println("\n✓ 轉牌河牌攤牌測試完成");
	}
	
//	@Test
	void testNextHandCycle() {
	    System.out.println("=== 測試下一手牌循環 ===");
	    
	    Player playerA = new Player("Alice", 1000);
	    Player playerB = new Player("Bob", 1000);
	    Player playerC = new Player("Charlie", 1000);
	    List<Player> players = Arrays.asList(playerA, playerB, playerC);
	    
	    GameState game = new GameState(players, 50, 100);
	    
	    System.out.println("第一手牌:");
	    game.startNewHand();
	    System.out.println("按鈕位置: " + game.getButtonIndex());
	    
	    // 檢查第一手的位置
	    for (int i = 0; i < players.size(); i++) {
	        Player p = players.get(i);
	        System.out.println(p.getName() + " (位置" + i + "): 按鈕=" + p.isButton() + 
	                         ", 小盲=" + p.isSmallBlind() + ", 大盲=" + p.isBigBlind());
	    }
	    
	    // 快速完成第一手牌
	    game.executePlayerAction(playerB, Action.call(100));
	    game.executePlayerAction(playerC, Action.call(100));
	    game.executePlayerAction(playerA, Action.check());
	    
	    // 快速完成所有階段到攤牌
	    while (game.getCurrentPhase() != GamePhase.SHOWDOWN) {
	        game.nextPhase();
	        while (!game.canNextPhase()) {
	            Player currentPlayer = game.getCurrentPlayer();
	            if (currentPlayer != null) {
	                game.executePlayerAction(currentPlayer, Action.check());
	            }
	        }
	    }
	    
	    System.out.println("\n第一手牌結束後籌碼:");
	    for (Player p : players) {
	        System.out.println(p.getName() + ": " + p.getChips() + " 籌碼");
	    }
	    
	    // 開始第二手牌
	    System.out.println("\n開始第二手牌:");
	    game.startNextHand();
	    System.out.println("按鈕位置: " + game.getButtonIndex());
	    System.out.println("當前階段: " + game.getCurrentPhase());
	    
	    // 檢查第二手的位置輪替
	    System.out.println("\n第二手位置分配:");
	    for (int i = 0; i < players.size(); i++) {
	        Player p = players.get(i);
	        System.out.println(p.getName() + " (位置" + i + "): 按鈕=" + p.isButton() + 
	                         ", 小盲=" + p.isSmallBlind() + ", 大盲=" + p.isBigBlind() +
	                         ", 籌碼=" + p.getChips() + ", 下注=" + p.getCurrentBet());
	    }
	    
	    System.out.println("\n第二手下注狀態:");
	    System.out.println("當前玩家: " + game.getCurrentPlayer().getName());
	    System.out.println("當前最高下注: " + game.getCurrentHighestBet());
	    System.out.println("底池總額: " + game.getPot().getTotalAmount());
	    
	    // 驗證
	    assertEquals(GamePhase.PREFLOP, game.getCurrentPhase());
	    assertNotNull(game.getCurrentPlayer());
	    assertTrue(game.getCurrentHighestBet() > 0);
	    assertEquals(0, game.getPot().getTotalAmount()); // 新手牌底池應該是空的
	    
	    System.out.println("\n✓ 下一手牌循環測試完成");
	}
	
	
	@Test
	void testComplexScenarios() {
	    System.out.println("=== 測試複雜情況（棄牌和全押）===");
	    
	    Player playerA = new Player("Alice", 200);  // 較少籌碼
	    Player playerB = new Player("Bob", 1000);
	    Player playerC = new Player("Charlie", 1000);
	    List<Player> players = Arrays.asList(playerA, playerB, playerC);
	    
	    GameState game = new GameState(players, 50, 100);
	    game.startNewHand();
	    
	    System.out.println("初始狀態:");
	    for (Player p : players) {
	        System.out.println(p.getName() + ": 籌碼=" + p.getChips() + ", 下注=" + p.getCurrentBet());
	    }
	    System.out.println("當前玩家: " + game.getCurrentPlayer().getName());
	    
	    // Bob 加注
	    System.out.println("\n1. Bob 加注到 300:");
	    boolean bobRaise = game.executePlayerAction(playerB, Action.raise(300));
	    System.out.println("執行成功: " + bobRaise);
	    System.out.println("Bob 籌碼: " + playerB.getChips() + ", 下注: " + playerB.getCurrentBet());
	    System.out.println("當前最高下注: " + game.getCurrentHighestBet());
	    
	    // Charlie 棄牌
	    System.out.println("\n2. Charlie 棄牌:");
	    boolean charlieFold = game.executePlayerAction(playerC, Action.fold());
	    System.out.println("執行成功: " + charlieFold);
	    System.out.println("Charlie 狀態: " + playerC.getStatus());
	    
	    // Alice 全押（籌碼不足跟注）
	    System.out.println("\n3. Alice 全押:");
	    boolean aliceAllIn = game.executePlayerAction(playerA, Action.allIn());
	    System.out.println("執行成功: " + aliceAllIn);
	    System.out.println("Alice 籌碼: " + playerA.getChips() + ", 下注: " + playerA.getCurrentBet());
	    System.out.println("Alice 狀態: " + playerA.getStatus());
	    
	    // Bob 跟注（應該只需要跟Alice的全押金額）
	    System.out.println("\n4. Bob 跟注Alice的全押:");
//	    System.out.println("當前玩家: " + game.getCurrentPlayer().getName());
	    System.out.println("是否可進入下階段: " + game.canNextPhase());
	    
	    // 如果還需要Bob行動
	    if (!game.canNextPhase() && game.getCurrentPlayer() != null) {
	        boolean bobCall = game.executePlayerAction(playerB, Action.call(game.getCurrentHighestBet()));
	        System.out.println("Bob 跟注執行成功: " + bobCall);
	    }
	    
	 // 快速到攤牌
	    while (game.getCurrentPhase() != GamePhase.SHOWDOWN) {
	        game.nextPhase();
	    }
	    
	    System.out.println("\n攤牌結果:");
	    System.out.println("底池總額: " + game.getPot().getTotalAmount());
	    System.out.println("邊池數量: " + game.getPot().getSidePotCount());
	    
	    System.out.println("\n最終籌碼:");
	    for (Player p : players) {
	        System.out.println(p.getName() + ": " + p.getChips() + " 籌碼 (狀態: " + p.getStatus() + ")");
	    }
	    
	    System.out.println("\n✓ 複雜情況測試完成");
	}
	
}
