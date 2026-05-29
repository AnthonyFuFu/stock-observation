-- ═══════════════════════════════════════════════════════════════
--  02_DB.sql  ─  App Schema + Concept Categories
--  Stock Observation Database  v2.0
--
--  包含資料表：
--    users / watchlists /
--    stock_concept_categories / stock_concept_relations
--
--  ⚠ 執行順序：
--    1. psql -f 01_DB.sql           ← 建立股票市場資料表
--    2. psql -f DB_twse_full.sql    ← 匯入全台股（2308 筆）
--    3. psql -f 02_DB.sql           ← 本檔：建立應用層表、概念股分類並寫入關聯
-- ═══════════════════════════════════════════════════════════════

-- ────────────────────────────────────────────────────────────────
-- 一、應用層資料表
-- ────────────────────────────────────────────────────────────────

-- 用戶表
CREATE TABLE IF NOT EXISTS users (
    id            SERIAL PRIMARY KEY,
    username      VARCHAR(50)  NOT NULL UNIQUE,
    email         VARCHAR(100) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    created_at    TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    last_login    TIMESTAMP WITH TIME ZONE
);

-- 自選股表
CREATE TABLE IF NOT EXISTS watchlists (
    id       SERIAL PRIMARY KEY,
    user_id  INTEGER NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    stock_id INTEGER NOT NULL REFERENCES stocks(id) ON DELETE CASCADE,
    added_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,
    notes    TEXT,
    UNIQUE (user_id, stock_id)
);

-- ────────────────────────────────────────────────────────────────
-- 二、概念股分類表
-- ────────────────────────────────────────────────────────────────

CREATE TABLE IF NOT EXISTS stock_concept_categories (
    id          SERIAL PRIMARY KEY,
    code        VARCHAR(20)  NOT NULL UNIQUE,  -- 分類代碼
    name        VARCHAR(50)  NOT NULL,          -- 中文名稱
    description TEXT,
    created_at  TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE IF NOT EXISTS stock_concept_relations (
    id          SERIAL PRIMARY KEY,
    stock_id    INTEGER      NOT NULL REFERENCES stocks(id) ON DELETE CASCADE,
    category_id INTEGER      NOT NULL REFERENCES stock_concept_categories(id) ON DELETE CASCADE,
    weight      DECIMAL(5,2) NOT NULL DEFAULT 1.0,  -- 關聯權重
    UNIQUE (stock_id, category_id)
);

CREATE INDEX IF NOT EXISTS idx_concept_cat_code  ON stock_concept_categories (code);
CREATE INDEX IF NOT EXISTS idx_concept_rel_stock ON stock_concept_relations (stock_id);
CREATE INDEX IF NOT EXISTS idx_concept_rel_cat   ON stock_concept_relations (category_id);

-- ────────────────────────────────────────────────────────────────
-- 三、概念股分類 INSERT（18 個分類）
-- ────────────────────────────────────────────────────────────────
INSERT INTO stock_concept_categories (code, name, description) VALUES
('AI',       'AI人工智能',     'AI晶片設計/製造、算力基礎設施、AI應用'),
('NVIDIA',   '輝達供應鏈',     'NVIDIA GB200/GB300 AI伺服器、CoWoS先進封裝供應鏈'),
('APPLE',    '蘋果概念股',     'Apple iPhone/Mac/iPad 零組件供應鏈'),
('TSMC_SC',  '台積電供應鏈',   '台積電先進製程材料、封裝測試、基板商'),
('EV',       '電動車',         '電動車零組件、充電樁、動力/熱管理系統'),
('5G',       '5G通信',         '5G網路設備、基站、終端晶片'),
('SERVER',   '伺服器',         'AI/雲端伺服器設計代工、電源、散熱'),
('SHIPPING', '航運',           '貨櫃航運、散裝航運、航空運輸'),
('BIOTECH',  '生技醫療',       '新藥開發、醫療器材、基因工程、精準醫療'),
('GREEN',    '綠能ESG',        '太陽能、風電、儲能、節能減碳'),
('HIGHDY',   '高股息',         '年化現金殖利率穩定高於 4%'),
('TW50',     '台灣50成分股',   '元大台灣50（0050）主要持股'),
('ABF',      'ABF載板',        'AI晶片先進封裝用ABF高階基板供應鏈'),
('CMOS',     'CMOS感測器',     '手機及車用相機影像感測器供應鏈'),
('CLOUD',    '雲端運算',       '雲端服務、資料中心建置及維運'),
('COOLING',  '散熱',           'AI伺服器液冷/風冷散熱解決方案'),
('HBM',      'HBM記憶體',      'AI高頻寬記憶體（HBM）設計製造'),
('DEFENSE',  '軍工航太',       '軍用電子、航太工業、國防採購受益')
ON CONFLICT (code) DO UPDATE SET
    name        = EXCLUDED.name,
    description = EXCLUDED.description;

-- ────────────────────────────────────────────────────────────────
-- 四、概念股關聯 INSERT
--     格式：(股票代號, 分類代碼)
--     安全：stock 或 category 不存在時自動略過
-- ────────────────────────────────────────────────────────────────
INSERT INTO stock_concept_relations (stock_id, category_id)
SELECT s.id, c.id FROM stocks s, stock_concept_categories c
WHERE (s.symbol, c.code) IN (
    -- ★ AI 人工智能
    ('2330','AI'),('2454','AI'),('2382','AI'),('2356','AI'),('3231','AI'),
    ('4938','AI'),('3711','AI'),('2308','AI'),('2379','AI'),('3037','AI'),
    ('3034','AI'),('3443','AI'),('5274','AI'),('6669','AI'),('3017','AI'),
    ('8046','AI'),('6789','AI'),
    -- ★ 輝達供應鏈
    ('2330','NVIDIA'),('3711','NVIDIA'),('3037','NVIDIA'),('2382','NVIDIA'),
    ('2308','NVIDIA'),('3533','NVIDIA'),('2356','NVIDIA'),('2303','NVIDIA'),
    ('6239','NVIDIA'),('8046','NVIDIA'),('5274','NVIDIA'),('6669','NVIDIA'),
    ('3017','NVIDIA'),('2360','NVIDIA'),
    -- ★ 蘋果概念股
    ('2317','APPLE'),('4938','APPLE'),('3008','APPLE'),('2474','APPLE'),
    ('2354','APPLE'),('2382','APPLE'),('2301','APPLE'),('3533','APPLE'),
    ('2327','APPLE'),('2395','APPLE'),('3037','APPLE'),('2357','APPLE'),
    -- ★ 台積電供應鏈
    ('3443','TSMC_SC'),('5347','TSMC_SC'),('6770','TSMC_SC'),('2449','TSMC_SC'),
    ('6239','TSMC_SC'),('3037','TSMC_SC'),('3533','TSMC_SC'),('2303','TSMC_SC'),
    ('2344','TSMC_SC'),('8046','TSMC_SC'),('6274','TSMC_SC'),
    -- ★ 電動車
    ('2308','EV'),('1590','EV'),('2049','EV'),('2201','EV'),('2207','EV'),
    ('2105','EV'),('3017','EV'),
    -- ★ 5G
    ('2412','5G'),('3045','5G'),('4904','5G'),('2454','5G'),
    ('4551','5G'),('2379','5G'),('3034','5G'),
    -- ★ 伺服器
    ('2382','SERVER'),('2356','SERVER'),('3231','SERVER'),('4938','SERVER'),
    ('2301','SERVER'),('2308','SERVER'),('3017','SERVER'),('6669','SERVER'),
    ('3706','SERVER'),('5274','SERVER'),
    -- ★ 航運
    ('2603','SHIPPING'),('2609','SHIPPING'),('2615','SHIPPING'),
    ('2606','SHIPPING'),('2610','SHIPPING'),('2618','SHIPPING'),
    ('2634','SHIPPING'),('2601','SHIPPING'),('2605','SHIPPING'),
    -- ★ 生技醫療
    ('1760','BIOTECH'),('4174','BIOTECH'),('4736','BIOTECH'),
    ('4151','BIOTECH'),('4726','BIOTECH'),('1762','BIOTECH'),
    -- ★ 綠能 ESG
    ('2308','GREEN'),('1590','GREEN'),('00850','GREEN'),
    -- ★ 高股息
    ('2412','HIGHDY'),('0056','HIGHDY'),('00878','HIGHDY'),('2002','HIGHDY'),
    ('5880','HIGHDY'),('2886','HIGHDY'),('1101','HIGHDY'),('2892','HIGHDY'),
    ('2880','HIGHDY'),('2603','HIGHDY'),('00919','HIGHDY'),('00929','HIGHDY'),
    -- ★ 台灣50成分股（依 2024Q4 持股）
    ('2330','TW50'),('2317','TW50'),('2454','TW50'),('2382','TW50'),
    ('2308','TW50'),('2412','TW50'),('3711','TW50'),('2881','TW50'),
    ('2882','TW50'),('1303','TW50'),('1301','TW50'),('2303','TW50'),
    ('2327','TW50'),('2357','TW50'),('2379','TW50'),('3008','TW50'),
    ('2002','TW50'),('2886','TW50'),('2891','TW50'),('1326','TW50'),
    ('2890','TW50'),('6505','TW50'),('2884','TW50'),('3231','TW50'),
    ('2885','TW50'),('2912','TW50'),('2603','TW50'),('2609','TW50'),
    ('1216','TW50'),('2049','TW50'),('2395','TW50'),('2207','TW50'),
    ('4938','TW50'),('1590','TW50'),('5880','TW50'),('3045','TW50'),
    -- ★ ABF 載板
    ('3037','ABF'),('8046','ABF'),('6274','ABF'),
    -- ★ CMOS 感測器
    ('3008','CMOS'),('2474','CMOS'),('6789','CMOS'),
    -- ★ 雲端運算
    ('2382','CLOUD'),('2356','CLOUD'),('3231','CLOUD'),('4938','CLOUD'),
    ('2412','CLOUD'),
    -- ★ 散熱
    ('3017','COOLING'),('2308','COOLING'),('2360','COOLING'),
    -- ★ HBM 記憶體
    ('2344','HBM'),('2408','HBM'),('2330','HBM'),('3711','HBM'),
    -- ★ 軍工航太
    ('2634','DEFENSE'),('2610','DEFENSE'),('2618','DEFENSE')
)
ON CONFLICT (stock_id, category_id) DO NOTHING;
