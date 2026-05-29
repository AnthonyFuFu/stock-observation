@echo off
chcp 65001 > nul
setlocal EnableDelayedExpansion

:: ─────────────────────────────────────────────
::  Stock Observation DB  一鍵初始化腳本
::  執行順序：01_DB → DB_twse_full → 02_DB
:: ─────────────────────────────────────────────

set PG_USER=postgres
set PG_HOST=localhost
set PG_PORT=5432
set PGPASSWORD=cga103g4

set PSQL=psql -U %PG_USER% -h %PG_HOST% -p %PG_PORT%
set SCRIPT_DIR=%~dp0

echo.
echo =========================================
echo   Stock Observation DB  一鍵初始化
echo =========================================
echo.
echo  PG Host  : %PG_HOST%:%PG_PORT%
echo  PG User  : %PG_USER%
echo  目標 DB  : stockdb
echo.

:: ── 01：重建 DB + 建立股票市場資料表 ────────
echo [1/3] 重建 stockdb ^& 建立股票市場資料表...
%PSQL% -f "%SCRIPT_DIR%01_DB.sql"
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] 01_DB.sql 執行失敗，請確認 PostgreSQL 是否已啟動
    echo         或 PG_USER / PGPASSWORD 是否正確
    goto :error
)
echo       完成

:: ── 02：匯入全台股 2308 筆 ──────────────────
echo.
echo [2/3] 匯入全台股資料（2308 筆，約需 30-60 秒）...
%PSQL% -d stockdb -f "%SCRIPT_DIR%DB_twse_full.sql"
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] DB_twse_full.sql 執行失敗
    goto :error
)
echo       完成

:: ── 03：建立應用層 + 概念股分類 ─────────────
echo.
echo [3/3] 建立應用層表 ^& 寫入概念股分類關聯...
%PSQL% -d stockdb -f "%SCRIPT_DIR%02_DB.sql"
if %ERRORLEVEL% NEQ 0 (
    echo.
    echo [ERROR] 02_DB.sql 執行失敗
    goto :error
)
echo       完成

echo.
echo =========================================
echo   全部完成！資料庫 stockdb 初始化成功
echo =========================================
echo.
goto :end

:error
echo.
echo =========================================
echo   初始化失敗，請查看上方錯誤訊息
echo =========================================
echo.
pause
exit /b 1

:end
endlocal
