# Stock Observation

一個以 **Spring Boot WebFlux（反應式 / Reactive）** 打造的股票觀測與直播平台，整合了台股（TWSE/TPEx）與美股（Finnhub）行情資料、股票概念股分類、以及直播 / 影片串流功能。

---

## 專案定位

這不是單純的股票 API 包裝專案，而是同時涵蓋三個領域的整合式練習專案：

1. **股票資料服務**：抓取、儲存、查詢台股與美股的即時報價、歷史股價、K 線資料，並支援「概念股分類」的多對多關聯查詢。
2. **直播串流服務**：透過 MediaMTX 做 RTMP 推流、HLS 播放代理，並用 WebSocket 實作直播間即時聊天室。
3. **影片點播服務**：支援本地影片與遠端影片的 HTTP Range 請求串流播放，以及後台影片上傳管理。

---

## 技術棧

| 分類 | 技術 |
| --- | --- |
| 核心框架 | Spring Boot 4（WebFlux，全反應式） |
| 資料庫存取 | Spring Data R2DBC（PostgreSQL / H2） |
| 即時通訊 | Spring WebSocket（直播聊天室） |
| 外部 API | Finnhub（美股）、TWSE 開放資料（台股） |
| 直播串流 | MediaMTX（RTMP → HLS / WebRTC） |
| 建置工具 | Maven（`mvnw` / `mvnw.cmd`） |
| 其他 | Lombok、Jackson、AOP（方法執行紀錄）、Logback（分離 console / 檔案輸出） |

---

## 模組總覽

### 📈 股票資料

| Controller | 說明 |
| --- | --- |
| `StockController` | 股票基本資料 CRUD、漲跌幅排行 |
| `StockPriceController` | 歷史股價查詢（依區間） |
| `StockQuoteController` | 即時報價 CRUD、熱門標的 |
| `StockConceptCategoryController` | 概念股分類（如：AI 概念股、半導體） |
| `StockConceptRelationController` | 股票 ↔ 概念股分類 的多對多關聯 |
| `StockImportController` | 一鍵匯入全台股（上市 + 上櫃）基本資料，首次執行約需 30–60 秒 |
| `FinnhubController` | 美股報價 / K 線 / 公司基本資料（Finnhub） |
| `TwseController` | 台股日成交資訊 / 大盤資訊 / 加權指數 K 線（TWSE） |

### 🔴 直播串流

| Controller | 說明 |
| --- | --- |
| `LiveRoomController` | 建立 / 查詢 / 關閉直播間、線上人數 |
| `LiveStreamController` | 代理 MediaMTX 的 HLS（`.m3u8` / `.ts`）輸出給瀏覽器 |
| `ChatWebSocketHandler` | 依直播間房號（roomId）分頻道的即時聊天室 |

### 🎬 影片點播

| Controller | 說明 |
| --- | --- |
| `VideoController` | 影片清單／中繼資料查詢 |
| `VideoStreamController` | 支援 Range 請求的本地 / 遠端影片串流播放 |
| `VideoAdminController` | 後台影片上傳管理 |

---

## 快速開始

### 1. 環境需求

- JDK 21
- PostgreSQL（或使用內建 H2 做本地測試）
- （直播功能需要）[MediaMTX](https://github.com/bluenviron/mediamtx/releases)、[FFmpeg](https://ffmpeg.org/)

### 2. 設定

專案設定集中在 [application.yml](src/main/resources/application.yml)，啟動前請確認：

- `spring.r2dbc.*`：資料庫連線資訊
- `finnhub.api-key`：Finnhub API 金鑰
- `app.video.upload-dir`：影片上傳存放目錄

### 3. 啟動專案

```powershell
.\mvnw.cmd spring-boot:run
```

啟動後預設監聽 `http://localhost:8080`，API 皆有 `/api` 前綴（見 `spring.webflux.base-path`）。

### 4. 常用頁面

```text
http://localhost:8080/api/index.html
http://localhost:8080/api/stock/index.html
http://localhost:8080/api/live/index.html
http://localhost:8080/api/live/watch.html?room=roomxxxxxx
http://localhost:8080/api/live/broadcast.html
http://localhost:8080/api/video/video.html
```

---

## 直播串流架構

```text
步驟 1                    步驟 2                     步驟 3                    步驟 4
開 CMD 視窗 1              開 CMD 視窗 2               開 CMD 視窗 3              開瀏覽器
    │                        │                         │                        │
    ▼                        ▼                         ▼                        ▼
cd C:\mediamtx          cd 影片資料夾               cd 專案資料夾             打開網址
mediamtx.exe            ffmpeg -re ...              mvn spring-boot:run     http://localhost:8080
    │                        │                         │                    /api/live.html
    │ 啟動成功                │ 開始推流                  │ Netty started       ?room=room001
    │ :1935 RTMP             │ → rtmp://               │ on port 8080           │
    │ :8888 HLS              │   localhost:1935         │                        │
    ▼                        ▼                         ▼                        ▼
  等待推流 ←──────── RTMP 推流 ──────→ 代理 HLS ──────────→ 🔴 看到直播！
```

### 操作步驟

1. **下載並啟動 MediaMTX**（[下載連結](https://github.com/bluenviron/mediamtx/releases)）

   ```powershell
   mediamtx.exe
   ```

   啟動成功會看到：

   ```text
   INF [RTMP] listener opened on :1935     # RTMP 推流入口（OBS / FFmpeg 推到這裡）
   INF [HLS] listener opened on :8888      # HLS 播放出口（瀏覽器從這裡看）
   INF [WebRTC] listener opened on :8889   # WebRTC（低延遲播放）
   ```

2. **安裝 FFmpeg 並推流**

   ```powershell
   winget install ffmpeg

   ffmpeg -re -stream_loop -1 -i C:\Users\user\git\stock-observation\src\main\resources\videos\sample.mp4 -c copy -f flv rtmp://localhost:1935/room001
   ```

3. **啟動本專案**

   ```powershell
   .\mvnw.cmd spring-boot:run
   ```

4. **瀏覽器觀看直播**

   ```text
   http://localhost:8080/api/live/watch.html?room=room001
   ```

---

## 影片串流架構

```text
                        ┌─────────────────────────────┐
                        │      Spring WebFlux          │
 瀏覽器/播放器           │                               │
 ┌──────────┐  HTTP     │  ┌─────────────────────────┐ │
 │  <video> │ ───────►  │  │  VideoStreamController  │ │
 │  元素     │ Range     │  └──────────┬──────────────┘ │
 └──────────┘ 請求      │             │                 │
                        │  ┌──────────▼──────────────┐ │
                        │  │   VideoStreamService     │ │
                        │  │                          │ │
                        │  │  ┌─────────┐ ┌────────┐ │ │
                        │  │  │ 本地檔案 │ │WebClient│ │ │
                        │  │  │ Resource │ │ 遠端URL │ │ │
                        │  │  └────┬────┘ └────┬───┘ │ │
                        │  └───────┼───────────┼─────┘ │
                        └──────────┼───────────┼───────┘
                                   │           │
                           ┌───────▼───┐  ┌────▼──────────┐
                           │ 本地磁碟   │  │  遠端影片伺服器 │
                           │ /videos/  │  │  (CDN/S3/...) │
                           └───────────┘  └───────────────┘
```

### 播放範例

```text
# 瀏覽器直接開本地資源
http://localhost:8080/api/video/local/sample.mp4

# 瀏覽器直接開網路影片串流
http://localhost:8080/api/video/remote?url=https://www.w3schools.com/html/mov_bbb.mp4
```

---

## 資料庫

- [01_DB.sql](01_DB.sql)：初始資料表結構
- [02_DB.sql](02_DB.sql)：後續擴充（概念股分類等）
- [DB_twse_full.sql](DB_twse_full.sql)：TWSE 全台股資料匯入用 SQL

## Log 設定

Console 只顯示 `INFO` 以上等級的訊息；SQL 除錯（R2DBC）與方法執行耗時等 `DEBUG` 細節統一寫入 `logs/stock-observation.log`（見 [logback-spring.xml](src/main/resources/logback-spring.xml)），避免開發時 console 被大量查詢訊息洗版。
