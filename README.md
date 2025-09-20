# Poker Game 後端專案

> 純後端 Java 撲克引擎，具自動流程控制、下注管理、REST API，易於前後端/AI/桌遊平台整合

## 目錄

- [技術特色](#技術特色)
- [系統架構](#系統架構)
- [核心功能](#核心功能)
- [API 設計](#api-設計)
- [技術亮點](#技術亮點)
- [運行方式](#運行方式)
- [使用範例](#使用範例)

## 技術特色

**後端架構**

- Spring Boot + RESTful API
- 領域驅動設計 (DDD)
- 完整的單元測試覆蓋

**核心算法實現**

- 智能下注輪管理
- 主池與邊池自動分配
- 手牌評估與比較算法
- 遊戲狀態自動推進

## 系統架構

```
com.chris.poker/
├── controller/     # REST API 控制器
├── domain/         # 核心業務邏輯
│   ├── GameState   # 遊戲狀態管理
│   ├── Player      # 玩家實體
│   ├── Pot         # 底池系統 (支援邊池)
│   └── BettingRound # 下注輪邏輯
├── card/           # 撲克牌系統
└── evaluation/     # 手牌評估
```

## 核心功能

**遊戲邏輯**

- 支援 2-8 人遊戲
- 完整的德州撲克規則
- 多階段遊戲流程 (翻牌前/翻牌/轉牌/河牌/攤牌)
- 自動遊戲進度控制

**下注系統**

- 所有標準動作：棄牌、過牌、跟注、下注、加注、全押
- 動態最小加注計算
- 複雜全押情況處理

**邊池算法**

- 自動創建主池與邊池
- 按下注能力分配獎金
- 支援多玩家不同金額全押

## API 設計

```http
# 遊戲管理
POST /api/game/create      # 創建遊戲
POST /api/game/start-hand  # 開始新手牌
GET  /api/game/state       # 查詢遊戲狀態

# 玩家動作
POST /api/game/action      # 執行動作
GET  /api/game/hand/{name} # 查詢手牌
```

## 技術亮點

**邊池分配算法**

```java
public void collectBets(List<Player> players) {
    // 按下注金額排序，為每個等級創建對應底池
    // 確保玩家只能贏取有資格的底池
}
```

**智能遊戲狀態管理**

- 自動檢測下注輪結束
- 動態推進遊戲階段
- 實時驗證動作合法性

**完整測試覆蓋**

- 單元測試涵蓋所有核心邏輯
- 邊界條件測試
- 複雜場景模擬

## 運行方式

```bash
git clone [repository-url]
cd poker-game
mvn spring-boot:run
```

訪問 API：`http://localhost:8080/api/game`

## 使用範例

創建遊戲：

```json
POST /api/game/create
{
  "players": [
    {"name": "Alice", "chips": 1000},
    {"name": "Bob", "chips": 1000}
  ],
  "smallBlind": 10,
  "bigBlind": 20
}
```

執行動作：

```json
POST /api/game/action
{
  "playerName": "Alice",
  "action": "CALL",
  "amount": 20
}
```

---

此專案展示了完整的後端開發能力，包括複雜業務邏輯實現、系統架構設計、API 開發和測試驅動開發等技術。
