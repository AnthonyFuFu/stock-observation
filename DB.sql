-- 股票基本信息表
CREATE TABLE stocks (
    id SERIAL PRIMARY KEY,
    symbol VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(100) NOT NULL,
    exchange VARCHAR(20) NOT NULL,
    sector VARCHAR(50),
    industry VARCHAR(50),
    listed_date DATE,
    description TEXT,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

-- 股票價格歷史表
CREATE TABLE stock_prices (
    id SERIAL PRIMARY KEY,
    stock_id INTEGER NOT NULL REFERENCES stocks(id),
    trade_date DATE NOT NULL,
    open_price DECIMAL(12,4) NOT NULL,
    high_price DECIMAL(12,4) NOT NULL,
    low_price DECIMAL(12,4) NOT NULL,
    close_price DECIMAL(12,4) NOT NULL,
    adjusted_close DECIMAL(12,4),
    volume BIGINT NOT NULL,
    UNIQUE (stock_id, trade_date)
);

-- 即時行情表 (最後更新的價格)
CREATE TABLE stock_quotes (
    id SERIAL PRIMARY KEY,
    stock_id INTEGER NOT NULL REFERENCES stocks(id),
    current_price DECIMAL(12,4) NOT NULL,
    price_change DECIMAL(12,4) NOT NULL,
    change_percent DECIMAL(8,4) NOT NULL,
    volume BIGINT NOT NULL,
    last_updated TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (stock_id)
);

-- 用戶表
CREATE TABLE users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    email VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_login TIMESTAMP WITH TIME ZONE
);

-- 用戶自選股表
CREATE TABLE watchlists (
    id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL REFERENCES users(id),
    stock_id INTEGER NOT NULL REFERENCES stocks(id),
    added_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    notes TEXT,
    UNIQUE (user_id, stock_id)
);

-- 指標表 (用於存儲技術指標)
CREATE TABLE stock_indicators (
    id SERIAL PRIMARY KEY,
    stock_id INTEGER NOT NULL REFERENCES stocks(id),
    indicator_date DATE NOT NULL,
    ma_5 DECIMAL(12,4),
    ma_20 DECIMAL(12,4),
    ma_50 DECIMAL(12,4),
    ma_200 DECIMAL(12,4),
    rsi_14 DECIMAL(8,4),
    macd DECIMAL(10,6),
    macd_signal DECIMAL(10,6),
    macd_histogram DECIMAL(10,6),
    UNIQUE (stock_id, indicator_date)
);