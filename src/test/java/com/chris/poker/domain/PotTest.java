package com.chris.poker.domain;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.*;


class PotTest {
    
    private Pot pot;
    private Player playerA;
    private Player playerB;
    private Player playerC;
    private Player playerD;
    
    @BeforeEach
    void setUp() {
        pot = new Pot();
        playerA = new Player("Alice", 1000);
        playerB = new Player("Bob", 1000);  
        playerC = new Player("Charlie", 1000);
        playerD = new Player("David", 1000);
    }
    
    @Test
    void testEmptyPot() {
        assertTrue(pot.isEmpty());
        assertEquals(0, pot.getTotalAmount());
        assertEquals(0, pot.getSidePotCount());
    }
    
    @Test
    void testSimpleMainPot() {
        System.out.println("=== testSimpleMainPot ===");
        // 三個玩家都下注100
        playerA.bet(100);
        playerB.bet(100); 
        playerC.bet(100);
        
        System.out.println("Before collectBets:");
        System.out.println("PlayerA currentBet: " + playerA.getCurrentBet() + ", chips: " + playerA.getChips());
        System.out.println("PlayerB currentBet: " + playerB.getCurrentBet() + ", chips: " + playerB.getChips());
        System.out.println("PlayerC currentBet: " + playerC.getCurrentBet() + ", chips: " + playerC.getChips());
        
        List<Player> players = Arrays.asList(playerA, playerB, playerC);
        pot.collectBets(players);
        
        System.out.println("After collectBets:");
        System.out.println("Total pot: " + pot.getTotalAmount());
        System.out.println("Side pot count: " + pot.getSidePotCount());
        for (int i = 0; i < pot.getSidePotCount(); i++) {
            Pot.SidePot sidePot = pot.getSidePots().get(i);
            System.out.println("Pot " + i + ": amount=" + sidePot.getAmount() + ", players=" + sidePot.getEligiblePlayers().size());
        }
        
        assertEquals(300, pot.getTotalAmount());
        assertEquals(1, pot.getSidePotCount());
        
        Pot.SidePot mainPot = pot.getSidePots().get(0);
        assertEquals(300, mainPot.getAmount());
        assertEquals(3, mainPot.getEligiblePlayers().size());
        assertTrue(mainPot.isEligible(playerA));
        assertTrue(mainPot.isEligible(playerB));
        assertTrue(mainPot.isEligible(playerC));
    }
    
    @Test
    void testSingleAllIn() {
        System.out.println("=== testSingleAllIn ===");
        // 玩家A全押100，B和C跟注100
        playerA = new Player("Alice", 100); // 只有100籌碼
        playerA.allin(); // 全押100
        
        playerB.call(100); // 跟注100
        playerC.call(100); // 跟注100
        
        System.out.println("Before collectBets:");
        System.out.println("PlayerA currentBet: " + playerA.getCurrentBet() + ", chips: " + playerA.getChips());
        System.out.println("PlayerB currentBet: " + playerB.getCurrentBet() + ", chips: " + playerB.getChips());
        System.out.println("PlayerC currentBet: " + playerC.getCurrentBet() + ", chips: " + playerC.getChips());
        
        List<Player> players = Arrays.asList(playerA, playerB, playerC);
        pot.collectBets(players);
        
        System.out.println("After collectBets:");
        System.out.println("Total pot: " + pot.getTotalAmount());
        System.out.println("Side pot count: " + pot.getSidePotCount());
        for (int i = 0; i < pot.getSidePotCount(); i++) {
            Pot.SidePot sidePot = pot.getSidePots().get(i);
            System.out.println("Pot " + i + ": amount=" + sidePot.getAmount() + ", players=" + sidePot.getEligiblePlayers().size());
        }
        
        assertEquals(300, pot.getTotalAmount());
        assertEquals(1, pot.getSidePotCount());
        
        Pot.SidePot mainPot = pot.getSidePots().get(0);
        assertEquals(300, mainPot.getAmount());
        assertTrue(mainPot.isEligible(playerA));
        assertTrue(mainPot.isEligible(playerB)); 
        assertTrue(mainPot.isEligible(playerC));
    }
    
    @Test
    void testTwoPlayerAllInDifferentAmounts() {
        System.out.println("=== testTwoPlayerAllInDifferentAmounts ===");
        // 玩家A: 50籌碼全押
        // 玩家B: 100籌碼全押
        // 玩家C: 跟注100
        playerA = new Player("Alice", 50);
        playerB = new Player("Bob", 100);
        
        playerA.allin(); // 全押50
        playerB.allin(); // 全押100  
        playerC.call(100); // 跟注100
        
        System.out.println("Before collectBets:");
        System.out.println("PlayerA currentBet: " + playerA.getCurrentBet() + ", chips: " + playerA.getChips());
        System.out.println("PlayerB currentBet: " + playerB.getCurrentBet() + ", chips: " + playerB.getChips());
        System.out.println("PlayerC currentBet: " + playerC.getCurrentBet() + ", chips: " + playerC.getChips());
        
        List<Player> players = Arrays.asList(playerA, playerB, playerC);
        pot.collectBets(players);
        
        System.out.println("After collectBets:");
        System.out.println("Total pot: " + pot.getTotalAmount());
        System.out.println("Side pot count: " + pot.getSidePotCount());
        for (int i = 0; i < pot.getSidePotCount(); i++) {
            Pot.SidePot sidePot = pot.getSidePots().get(i);
            System.out.println("Pot " + i + ": amount=" + sidePot.getAmount() + ", players=" + sidePot.getEligiblePlayers().size());
            for (Player p : sidePot.getEligiblePlayers()) {
                System.out.println("  - " + p.getName());
            }
        }
        
        assertEquals(250, pot.getTotalAmount());
        assertEquals(2, pot.getSidePotCount());
        
        // 主池: 50 * 3 = 150 (所有人都能競爭)
        Pot.SidePot mainPot = pot.getSidePots().get(0);
        assertEquals(150, mainPot.getAmount());
        assertTrue(mainPot.isEligible(playerA));
        assertTrue(mainPot.isEligible(playerB));
        assertTrue(mainPot.isEligible(playerC));
        
        // 邊池: (100-50) * 2 = 100 (只有B和C能競爭)
        Pot.SidePot sidePot = pot.getSidePots().get(1);
        assertEquals(100, sidePot.getAmount());
        assertFalse(sidePot.isEligible(playerA));
        assertTrue(sidePot.isEligible(playerB));
        assertTrue(sidePot.isEligible(playerC));
    }
    
    @Test
    void testComplexMultipleSidePots() {
        System.out.println("=== testComplexMultipleSidePots ===");
        // 玩家A: 30籌碼全押
        // 玩家B: 80籌碼全押
        // 玩家C: 150籌碼全押
        // 玩家D: 跟注150
        playerA = new Player("Alice", 30);
        playerB = new Player("Bob", 80);
        playerC = new Player("Charlie", 150);
        
        playerA.allin(); // 全押30
        playerB.allin(); // 全押80
        playerC.allin(); // 全押150
        playerD.call(150); // 跟注150
        
        System.out.println("Before collectBets:");
        System.out.println("PlayerA currentBet: " + playerA.getCurrentBet() + ", chips: " + playerA.getChips());
        System.out.println("PlayerB currentBet: " + playerB.getCurrentBet() + ", chips: " + playerB.getChips());
        System.out.println("PlayerC currentBet: " + playerC.getCurrentBet() + ", chips: " + playerC.getChips());
        System.out.println("PlayerD currentBet: " + playerD.getCurrentBet() + ", chips: " + playerD.getChips());
        
        List<Player> players = Arrays.asList(playerA, playerB, playerC, playerD);
        pot.collectBets(players);
        
        System.out.println("After collectBets:");
        System.out.println("Total pot: " + pot.getTotalAmount());
        System.out.println("Side pot count: " + pot.getSidePotCount());
        for (int i = 0; i < pot.getSidePotCount(); i++) {
            Pot.SidePot sidePot = pot.getSidePots().get(i);
            System.out.println("Pot " + i + ": amount=" + sidePot.getAmount() + ", players=" + sidePot.getEligiblePlayers().size());
            for (Player p : sidePot.getEligiblePlayers()) {
                System.out.println("  - " + p.getName());
            }
        }
        
        assertEquals(410, pot.getTotalAmount());
        assertEquals(3, pot.getSidePotCount());
        
        // 主池: 30 * 4 = 120 (所有人都能競爭)
        Pot.SidePot mainPot = pot.getSidePots().get(0);
        assertEquals(120, mainPot.getAmount());
        assertTrue(mainPot.isEligible(playerA));
        assertTrue(mainPot.isEligible(playerB));
        assertTrue(mainPot.isEligible(playerC));
        assertTrue(mainPot.isEligible(playerD));
        
        // 邊池1: (80-30) * 3 = 150 (B, C, D能競爭)
        Pot.SidePot sidePot1 = pot.getSidePots().get(1);
        assertEquals(150, sidePot1.getAmount());
        assertFalse(sidePot1.isEligible(playerA));
        assertTrue(sidePot1.isEligible(playerB));
        assertTrue(sidePot1.isEligible(playerC));
        assertTrue(sidePot1.isEligible(playerD));
        
        // 邊池2: (150-80) * 2 = 140 (只有C, D能競爭)
        Pot.SidePot sidePot2 = pot.getSidePots().get(2);
        assertEquals(140, sidePot2.getAmount());
        assertFalse(sidePot2.isEligible(playerA));
        assertFalse(sidePot2.isEligible(playerB));
        assertTrue(sidePot2.isEligible(playerC));
        assertTrue(sidePot2.isEligible(playerD));
    }
    
    @Test
    void testSingleWinnerPayout() {
        System.out.println("=== testSingleWinnerPayout ===");
        // 設置簡單情況
        playerA.bet(100);
        playerB.bet(100);
        
        System.out.println("Before collectBets:");
        System.out.println("PlayerA currentBet: " + playerA.getCurrentBet() + ", chips: " + playerA.getChips());
        System.out.println("PlayerB currentBet: " + playerB.getCurrentBet() + ", chips: " + playerB.getChips());
        
        List<Player> players = Arrays.asList(playerA, playerB);
        pot.collectBets(players);
        
        System.out.println("After collectBets:");
        System.out.println("Total pot: " + pot.getTotalAmount());
        
        int originalChipsA = playerA.getChips();
        System.out.println("PlayerA chips before winning: " + originalChipsA);
        
        int winnings = pot.awardToWinner(playerA);
        
        System.out.println("Winnings: " + winnings);
        System.out.println("PlayerA chips after winning: " + playerA.getChips());
        
        assertEquals(200, winnings);
        assertEquals(originalChipsA + 200, playerA.getChips());
        assertTrue(pot.isEmpty());
    }
    
    @Test
    void testSplitPot() {
        // 設置平分情況
        playerA.bet(100);
        playerB.bet(100);
        playerC.bet(100);
        
        List<Player> players = Arrays.asList(playerA, playerB, playerC);
        pot.collectBets(players);
        
        int originalChipsA = playerA.getChips();
        int originalChipsB = playerB.getChips();
        
        List<Player> winners = Arrays.asList(playerA, playerB);
        pot.splitPot(winners);
        
        // 300 / 2 = 150 each
        assertEquals(originalChipsA + 150, playerA.getChips());
        assertEquals(originalChipsB + 150, playerB.getChips());
        assertTrue(pot.isEmpty());
    }
    
    @Test
    void testSplitPotWithRemainder() {
        // 測試有餘數的情況
        playerA.bet(50);
        playerB.bet(50);
        playerC.bet(50);
        
        List<Player> players = Arrays.asList(playerA, playerB, playerC);
        pot.collectBets(players);
        
        int originalChipsA = playerA.getChips();
        int originalChipsB = playerB.getChips();
        
        // 150 / 2 = 75 each, 餘數0
        List<Player> winners = Arrays.asList(playerA, playerB);
        pot.splitPot(winners);
        
        assertEquals(originalChipsA + 75, playerA.getChips());
        assertEquals(originalChipsB + 75, playerB.getChips());
    }
    
    @Test
    void testDistributePotsWithSidePots() {
        System.out.println("=== testDistributePotsWithSidePots ===");
        // 複雜的邊池分配測試
        playerA = new Player("Alice", 50);
        playerB = new Player("Bob", 100);
        
        playerA.allin(); // 50
        playerB.allin(); // 100
        playerC.call(100); // 100
        
        System.out.println("Before collectBets:");
        System.out.println("PlayerA currentBet: " + playerA.getCurrentBet() + ", chips: " + playerA.getChips());
        System.out.println("PlayerB currentBet: " + playerB.getCurrentBet() + ", chips: " + playerB.getChips());
        System.out.println("PlayerC currentBet: " + playerC.getCurrentBet() + ", chips: " + playerC.getChips());
        
        List<Player> players = Arrays.asList(playerA, playerB, playerC);
        pot.collectBets(players);
        
        System.out.println("After collectBets:");
        System.out.println("Total pot: " + pot.getTotalAmount());
        System.out.println("Side pot count: " + pot.getSidePotCount());
        for (int i = 0; i < pot.getSidePotCount(); i++) {
            Pot.SidePot sidePot = pot.getSidePots().get(i);
            System.out.println("Pot " + i + ": amount=" + sidePot.getAmount() + ", players=" + sidePot.getEligiblePlayers().size());
        }
        
        // 創建贏家列表：主池playerA贏，邊池playerB贏
        List<List<Player>> winners = Arrays.asList(
            Arrays.asList(playerA), // 主池贏家
            Arrays.asList(playerB)  // 邊池贏家
        );
        
        int originalChipsA = playerA.getChips();
        int originalChipsB = playerB.getChips();
        
        System.out.println("Before distribution:");
        System.out.println("PlayerA chips: " + originalChipsA);
        System.out.println("PlayerB chips: " + originalChipsB);
        
        List<WinnerInfo> distribution = pot.distributePots(winners);
        
        System.out.println("Distribution result:");
//        for (Map.Entry<Player, Integer> entry : distribution.entrySet()) {
//            System.out.println(entry.getKey().getName() + " won: " + entry.getValue());
//        }
//        
        System.out.println("After distribution:");
        System.out.println("PlayerA chips: " + playerA.getChips());
        System.out.println("PlayerB chips: " + playerB.getChips());
        
        // 主池150給playerA，邊池100給playerB
//        assertEquals(150, distribution.get(playerA).intValue());
//        assertEquals(100, distribution.get(playerB).intValue());
        assertEquals(originalChipsA + 150, playerA.getChips());
        assertEquals(originalChipsB + 100, playerB.getChips());
    }
    
    @Test
    void testAddToPotWithEmptyEligiblePlayers() {
        // 測試邊界條件
        assertThrows(IllegalArgumentException.class, () -> {
            pot.addtoPot(100, Arrays.asList());
        });
    }
    
    @Test
    void testToString() {
        assertTrue(pot.toString().contains("Empty Pot"));
        
        playerA.bet(100);
        playerB.bet(100);
        List<Player> players = Arrays.asList(playerA, playerB);
        pot.collectBets(players);
        
        String result = pot.toString();
        assertTrue(result.contains("Total Pot: 200"));
        assertTrue(result.contains("Main Pot: 200"));
    }
}