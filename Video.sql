-- ────────────────────────────────────────────────────────────────
-- 一、建立資料表
-- ────────────────────────────────────────────────────────────────

-- 影片資料表（取代原本的 videos/metadata.json，方便日後擴充欄位與查詢）
CREATE TABLE IF NOT EXISTS videos (
    id           SERIAL PRIMARY KEY,                                      -- 自動遞增主鍵
    filename     VARCHAR(255) NOT NULL UNIQUE,                            -- 影片檔名（對應 resources/videos/ 下的實體檔案）
    title        VARCHAR(200) NOT NULL,                                   -- 標題
    description  TEXT,                                                    -- 簡介
    category     VARCHAR(50)  NOT NULL DEFAULT '未分類',                  -- 分類
    tags         TEXT[],                                                  -- 標籤（陣列）
    release_date DATE,                                                    -- 發布日期
    created_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP,      -- 建立時間
    updated_at   TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP       -- 最後更新時間
);

CREATE INDEX IF NOT EXISTS idx_videos_category ON videos (category);

-- ────────────────────────────────────────────────────────────────
-- 二、影片資料 INSERT
--     新增影片時：把檔案放進 resources/videos/，並在此表新增一筆資料即可
-- ────────────────────────────────────────────────────────────────
INSERT INTO videos (filename, title, description, category, tags, release_date) VALUES
('sample.mp4', '範例影片', '系統內建的範例影片，可用來測試播放與串流功能。', '範例', ARRAY['demo','sample'], '2026-01-01'),
('1.mp4', '影片 1', '請編輯資料庫中的這筆資料來更新標題與介紹。', '未分類', ARRAY[]::TEXT[], '2026-01-01'),
('2.mp4', '影片 2', '請編輯資料庫中的這筆資料來更新標題與介紹。', '未分類', ARRAY[]::TEXT[], '2026-01-01'),
('3.mp4', '影片 3', '請編輯資料庫中的這筆資料來更新標題與介紹。', '未分類', ARRAY[]::TEXT[], '2026-01-01'),
('4.mp4', '影片 4', '請編輯資料庫中的這筆資料來更新標題與介紹。', '未分類', ARRAY[]::TEXT[], '2026-01-01'),
('5.mp4', '影片 5', '請編輯資料庫中的這筆資料來更新標題與介紹。', '未分類', ARRAY[]::TEXT[], '2026-01-01'),
('6.mp4', '影片 6', '請編輯資料庫中的這筆資料來更新標題與介紹。', '未分類', ARRAY[]::TEXT[], '2026-01-01')
ON CONFLICT (filename) DO UPDATE SET
    title        = EXCLUDED.title,
    description  = EXCLUDED.description,
    category     = EXCLUDED.category,
    tags         = EXCLUDED.tags,
    release_date = EXCLUDED.release_date,
    updated_at   = CURRENT_TIMESTAMP;
