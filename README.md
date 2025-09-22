# Poker Game 後端專案

> 純後端 Java 撲克引擎，支援多裝置/跨平台連線對戰，具自動流程控制、下注管理、WebSocket 即時同步、REST API，易於前後端/AI/桌遊平台整合

## 目錄

- [技術特色](#技術特色)
- [系統架構](#系統架構)
- [核心功能](#核心功能)
- [API 設計](#api-設計)
- [技術亮點](#技術亮點)
- [運行方式](#運行方式)
- [使用範例](#使用範例)
- [線上對戰](#線上對戰)

## 技術特色

**後端架構**

- Spring Boot + RESTful API
- WebSocket 即時通訊 - 遊戲狀態即時廣播
- 領域驅動設計 (DDD) - 清晰的業務邏輯分層
- JWT Token 認證 - 安全的玩家身份驗證
- 完整的單元測試覆蓋
- 支援多裝置跨平台連線對戰

**核心算法實現**

- 智能下注輪管理 - 自動檢測回合完成
- 主池與邊池自動分配 - 支援複雜全押場景
- 手牌評估與比較算法 - 7 張牌最佳組合
- 遊戲狀態自動推進 - 無需手動控制階段轉換
- 動態座位系統 - 靈活的玩家加入/離開機制

## 系統架構

```
com.chris.poker/
├── controller/          # REST API 控制器
│   ├── PokerGameController     # 遊戲邏輯 API
│   └── GlobalExceptionHandler  # 統一異常處理
├── domain/             # 核心業務邏輯
│   ├── GameState       # 遊戲狀態管理
│   ├── Player          # 玩家實體
│   ├── Pot             # 底池系統 (支援邊池)
│   ├── BettingRound    # 下注輪邏輯
│   └── Action          # 玩家動作封裝
├── card/               # 撲克牌系統
│   ├── Card            # 撲克牌實體
│   ├── Deck            # 牌組管理
│   ├── Hand            # 玩家手牌
│   └── Board           # 公共牌
├── evaluation/         # 手牌評估
│   ├── HandEvaluator   # 牌型判斷
│   └── HandRank        # 牌型排名
├── seat/               # 座位管理系統
│   ├── SeatManager     # 座位分配與管理
│   └── SeatInfo        # 座位信息
├── service/            # 業務服務層
│   ├── TokenService    # JWT Token 管理
│   └── GameBroadcastService  # WebSocket 廣播
├── dto/                # 數據傳輸對象
└── websocket/          # WebSocket 處理器
```

## 核心功能

**遊戲邏輯**

- 支援 2-8 人遊戲
- 完整的德州撲克規則
- 多階段遊戲流程 (翻牌前/翻牌/轉牌/河牌/攤牌)
- 自動遊戲進度控制
- 動態座位系統

**下注系統**

- 所有標準動作：棄牌、過牌、跟注、下注、加注、全押
- 動態最小加注計算
- 複雜全押情況處理
- 實時動作驗證

**邊池算法**

- 自動創建主池與邊池
- 按下注能力分配獎金
- 支援多玩家不同金額全押
- 精確的獎金分配邏輯

**即時通訊**

- WebSocket 遊戲狀態廣播
- 座位變化即時同步
- 玩家動作即時通知
- 階段轉換自動推送

## API 設計

**座位管理**

```http
POST   /api/game/join-seat    # 加入座位 (返回 JWT Token)
POST   /api/game/leave-seat   # 離開座位
GET    /api/game/seats        # 查詢座位狀態
```

**遊戲管理**

```http
POST   /api/game/create       # 創建遊戲
POST   /api/game/start-hand   # 開始新手牌
POST   /api/game/end          # 結束遊戲
GET    /api/game/state        # 查詢遊戲狀態
```

**玩家操作 (需要 JWT Token)**

```http
POST   /api/game/action       # 執行動作 (需認證)
GET    /api/game/hand         # 查詢手牌 (需認證)
```

**WebSocket 端點**

```
ws://localhost:8080/ws/game   # 開發環境
wss://campusplus.xyz/ws/game  # 生產環境
```

## 技術亮點

**1. 智能座位系統**

```java
@Component
public class SeatManager {
    // 動態座位分配，支援玩家隨時加入/離開
    // 自動轉換為遊戲玩家列表
    // 防止重複名稱和座位衝突
}
```

**2. 邊池分配算法**

```java
public void collectBets(List<Player> players) {
    // 按下注金額排序，為每個等級創建對應底池
    // 確保玩家只能贏取有資格的底池
    // 支援複雜的多人全押場景
}
```

**3. 智能遊戲狀態管理**

```java
public class BettingRound {
    // 自動檢測下注輪結束
    // 追蹤每個玩家的行動狀態
    // 處理盲注的特殊邏輯
    // 防止無效動作導致的狀態錯誤
}
```

**4. WebSocket 即時廣播**

```java
@Service
public class GameBroadcastService {
    // SEATS_UPDATE: 座位變化廣播
    // GAME_STATE_UPDATE: 遊戲狀態同步
    // PLAYER_ACTION: 玩家動作通知
    // PHASE_CHANGE: 階段轉換推送
}
```

**5. JWT Token 安全認證**

```java
@Service
public class TokenService {
    // 玩家加入座位時生成 Token
    // 查看手牌和執行動作需要 Token 驗證
    // 30 分鐘自動過期機制
}
```

**6. 完整測試覆蓋**

- 單元測試涵蓋所有核心邏輯
- 邊界條件測試
- 複雜場景模擬

## 運行方式

**本地開發**

```bash
git clone [repository-url]
cd poker-game-backend
mvn spring-boot:run
```

訪問 API：`http://localhost:8080/api/game`  
WebSocket：`ws://localhost:8080/ws/game`

**生產部署**

後端透過 Cloudflare Tunnel 部署到 `https://campusplus.xyz`

## 使用範例

**1. 玩家加入座位**

```json
POST /api/game/join-seat
{
  "seatNumber": 0,
  "playerName": "Alice",
  "chips": 1000
}

Response:
{
  "success": true,
  "data": {
    "seatInfo": {...},
    "token": "eyJhbGc..."  // JWT Token
  }
}
```

**2. 創建遊戲**

```json
POST /api/game/create
{
  "smallBlind": 10,
  "bigBlind": 20
}
```

**3. 開始新手牌**

```json
POST /api/game/start-hand
```

**4. 執行動作**

```json
POST /api/game/action
Headers:
  Authorization: Bearer eyJhbGc...

Body:
{
  "action": "RAISE",
  "amount": 50
}
```

**5. 查看手牌**

```json
GET /api/game/hand
Headers:
  Authorization: Bearer eyJhbGc...

Response:
{
  "success": true,
  "data": {
    "cards": ["ACE_SPADES", "KING_HEARTS"]
  }
}
```

## 線上對戰

**多裝置同步對戰**

支援多裝置/多瀏覽器同步對戰
玩家可透過手機、平板、電腦等不同裝置登入同一局遊戲，跨平台、即時同步狀態

**多玩家同時操作**

前端部署於 Vercel (`https://poker-game-vue.vercel.app`)
後端服務透過 Cloudflare Tunnel 對外公開 (`https://campusplus.xyz`)
所有裝置只要連網即可參與對戰

**WebSocket 即時互動**

支援 WebSocket (`wss://`) 即時互動
遊戲過程訊息、動作同步更即時流暢
座位變化、狀態更新自動推送

**進階整合**

後端 API 及 WebSocket 端點皆可被任意裝置安全連接
前後端嚴謹 CORS 設定，保證跨域訪問安全
JWT Token 認證機制，確保玩家身份安全
支援生產環境 HTTPS/WSS 加密連接

**部署架構**

```
前端 (Vercel)
    ↓ HTTPS/WSS
後端 (Cloudflare Tunnel) → Spring Boot 應用
    ↓
數據庫/狀態管理
```

---

**技術棧總結**

後端技術

- Spring Boot 3.x
- WebSocket (原生 WebSocket)
- JWT Token 認證
- RESTful API 設計
- 領域驅動設計 (DDD)

部署方案

- Cloudflare Tunnel (後端)
- Vercel (前端)
- 支援 HTTPS/WSS 安全連接

測試與品質

- JUnit 5 單元測試
- 邊界條件覆蓋
- 複雜場景驗證

---

此專案展示了完整的後端開發能力，包括：

- 複雜業務邏輯實現（撲克遊戲規則、邊池算法）
- 系統架構設計（DDD、分層架構）
- API 開發（RESTful、WebSocket）
- 安全認證（JWT Token）
- 實時通訊（WebSocket 廣播）
- 測試驅動開發（TDD）
- 生產環境部署（Cloudflare Tunnel）
