-- ────────────────────────────────────────────────────────────────
-- 一、建立資料表
-- ────────────────────────────────────────────────────────────────

-- 股票基本資料表
CREATE TABLE IF NOT EXISTS stocks (
    id                  SERIAL PRIMARY KEY,
    symbol              VARCHAR(20)  NOT NULL UNIQUE,          -- 股票代號 (e.g. 2330, AAPL)
    name                VARCHAR(100) NOT NULL,                  -- 股票名稱
    exchange            VARCHAR(10)  NOT NULL,                  -- 交易所：TWSE / TPEx / NYSE / NASDAQ
    market_type         VARCHAR(10)  NOT NULL DEFAULT 'TWSE',  -- 市場別：TWSE上市 / TPEx上櫃
    isin_code           VARCHAR(20),                            -- 國際證券識別碼 (TW0002330008)
    currency            VARCHAR(10)  NOT NULL DEFAULT 'TWD',   -- 幣別
    par_value           DECIMAL(10,2),                          -- 面額（元）
    sector              VARCHAR(50),                            -- 產業別 (e.g. 半導體業)
    industry            VARCHAR(80),                            -- 細分產業 (e.g. 積體電路設計)
    capital             BIGINT,                                  -- 實收資本額（千元）
    shares_outstanding  BIGINT,                                  -- 流通在外股數（股）
    listed_date         DATE,                                    -- 掛牌日期
    is_active           BOOLEAN      NOT NULL DEFAULT TRUE,     -- 是否正常交易
    description         TEXT,                                                -- 公司簡介（可選）
    created_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,  -- 建立時間
    updated_at          TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP   -- 最後更新時間
);

-- 股票價格歷史表
CREATE TABLE IF NOT EXISTS stock_prices (
    id             SERIAL PRIMARY KEY,                                        -- 自動遞增主鍵
    stock_id       INTEGER       NOT NULL REFERENCES stocks(id) ON DELETE CASCADE, -- 對應 stocks.id
    trade_date     DATE          NOT NULL,                                    -- 交易日期
    open_price     DECIMAL(12,4) NOT NULL,                                    -- 開盤價
    high_price     DECIMAL(12,4) NOT NULL,                                    -- 最高價
    low_price      DECIMAL(12,4) NOT NULL,                                    -- 最低價
    close_price    DECIMAL(12,4) NOT NULL,                                    -- 收盤價
    adjusted_close DECIMAL(12,4),                                             -- 還原價（考慮除權息後調整）
    change_price   DECIMAL(12,4),                -- 漲跌金額（TWSE 直接提供）
    volume         BIGINT        NOT NULL,        -- 成交股數
    turnover       BIGINT,                        -- 成交金額（元）
    transactions   INTEGER,                       -- 成交筆數
    UNIQUE (stock_id, trade_date)
);

-- 即時行情表
CREATE TABLE IF NOT EXISTS stock_quotes (
    id             SERIAL PRIMARY KEY,                                        -- 自動遞增主鍵（每股一列）
    stock_id       INTEGER       NOT NULL REFERENCES stocks(id) ON DELETE CASCADE, -- 對應 stocks.id（UNIQUE 確保一股一列）
    open_price     DECIMAL(12,4),                 -- 開盤價
    high_price     DECIMAL(12,4),                 -- 最高價
    low_price      DECIMAL(12,4),                 -- 最低價
    current_price  DECIMAL(12,4) NOT NULL,        -- 現價
    prev_close     DECIMAL(12,4),                 -- 昨日收盤
    price_change   DECIMAL(12,4) NOT NULL,        -- 漲跌金額
    change_percent DECIMAL(8,4)  NOT NULL,        -- 漲跌幅（%）
    volume         BIGINT        NOT NULL,        -- 成交股數
    turnover       BIGINT,                        -- 成交金額（元）
    last_updated   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,       -- 最後更新時間
    UNIQUE (stock_id)
);

-- 技術指標表
CREATE TABLE IF NOT EXISTS stock_indicators (
    id               SERIAL PRIMARY KEY,
    stock_id         INTEGER NOT NULL REFERENCES stocks(id) ON DELETE CASCADE,
    indicator_date   DATE    NOT NULL,
    -- 均線
    ma_5             DECIMAL(12,4),    -- 5日均線（週線）
    ma_10            DECIMAL(12,4),    -- 10日均線
    ma_20            DECIMAL(12,4),    -- 20日均線（月線）
    ma_50            DECIMAL(12,4),    -- 50日均線
    ma_60            DECIMAL(12,4),    -- 60日均線（季線）
    ma_120           DECIMAL(12,4),    -- 120日均線（半年線）
    ma_200           DECIMAL(12,4),    -- 200日均線
    ma_240           DECIMAL(12,4),    -- 240日均線（年線）
    -- RSI
    rsi_14           DECIMAL(8,4),
    -- MACD
    macd             DECIMAL(10,6),
    macd_signal      DECIMAL(10,6),
    macd_histogram   DECIMAL(10,6),
    -- KD
    kdj_k            DECIMAL(8,4),     -- KD K值
    kdj_d            DECIMAL(8,4),     -- KD D值
    kdj_j            DECIMAL(8,4),     -- KD J值（3K-2D）
    -- 布林通道（Bollinger Bands, 20MA ± 2σ）
    bollinger_upper  DECIMAL(12,4),
    bollinger_middle DECIMAL(12,4),
    bollinger_lower  DECIMAL(12,4),
    -- 乖離率
    bias_5           DECIMAL(8,4),     -- 5日乖離率（%）
    bias_20          DECIMAL(8,4),     -- 20日乖離率（%）
    -- 量能均線
    volume_ma_5      BIGINT,
    volume_ma_20     BIGINT,
    UNIQUE (stock_id, indicator_date)
);

-- 除權息記錄表
CREATE TABLE IF NOT EXISTS dividends (
    id              SERIAL PRIMARY KEY,
    stock_id        INTEGER       NOT NULL REFERENCES stocks(id) ON DELETE CASCADE,
    year            INTEGER       NOT NULL,        -- 所屬年度
    ex_date         DATE,                          -- 除息/除權日
    record_date     DATE,                          -- 基準日
    pay_date        DATE,                          -- 發放日
    cash_dividend   DECIMAL(10,4),                 -- 現金股利（元/股）
    stock_dividend  DECIMAL(10,4),                 -- 股票股利（元/股）
    total_dividend  DECIMAL(10,4)                  -- 合計（元/股）
        GENERATED ALWAYS AS (COALESCE(cash_dividend,0) + COALESCE(stock_dividend,0)) STORED,
    yield_rate      DECIMAL(8,4),                  -- 殖利率（%），以除息日前收盤計算
    created_at      TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (stock_id, year, ex_date)
);

-- 財務報表指標表（季報/年報）
CREATE TABLE IF NOT EXISTS stock_financials (
    id               SERIAL PRIMARY KEY,
    stock_id         INTEGER NOT NULL REFERENCES stocks(id) ON DELETE CASCADE,
    report_year      INTEGER NOT NULL,
    report_quarter   INTEGER,             -- 1~4；NULL = 年報
    revenue          BIGINT,              -- 營業收入（千元）
    gross_profit     BIGINT,              -- 毛利（千元）
    operating_income BIGINT,              -- 營業利益（千元）
    net_income       BIGINT,              -- 稅後淨利（千元）
    eps              DECIMAL(10,4),       -- 每股盈餘（元）
    bvps             DECIMAL(10,4),       -- 每股淨值（元）
    roe              DECIMAL(8,4),        -- 股東權益報酬率（%）
    roa              DECIMAL(8,4),        -- 資產報酬率（%）
    gross_margin     DECIMAL(8,4),        -- 毛利率（%）
    op_margin        DECIMAL(8,4),        -- 營業利益率（%）
    net_margin       DECIMAL(8,4),        -- 淨利率（%）
    per              DECIMAL(10,4),       -- 本益比（報告期末）
    pbr              DECIMAL(10,4),       -- 股價淨值比
    created_at       TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (stock_id, report_year, report_quarter)
);

-- ────────────────────────────────────────────────────────────────
-- 二、索引
-- ────────────────────────────────────────────────────────────────
CREATE INDEX IF NOT EXISTS idx_stocks_market_type ON stocks (market_type);
CREATE INDEX IF NOT EXISTS idx_stocks_sector      ON stocks (sector);
CREATE INDEX IF NOT EXISTS idx_stocks_is_active   ON stocks (is_active) WHERE is_active = TRUE;
CREATE INDEX IF NOT EXISTS idx_prices_date        ON stock_prices (trade_date DESC);
CREATE INDEX IF NOT EXISTS idx_quotes_updated     ON stock_quotes (last_updated DESC);
