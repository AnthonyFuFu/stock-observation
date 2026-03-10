package com.anthonyfu;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
public class StockData {
    public static void main(String[] args) {
        System.out.println("===== 台灣證券交易所 API 測試 =====");
        
        // 測試個股日成交資訊
        testStockDaily();
        
        // 測試大盤資訊
        testMarketInfo();
        
        // 測試公司基本資料查詢
        testCompanyInfo("台積電");
    }
    
    private static void testStockDaily() {
        try {
            System.out.println("\n----- 測試個股日成交資訊 (台積電 2330) -----");
            
            String stockCode = "2330"; // 台積電
            LocalDate today = LocalDate.now();
            String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
            
            String stockDailyUrl = "https://www.twse.com.tw/exchangeReport/STOCK_DAY?response=json&date=" 
                                + formattedDate + "&stockNo=" + stockCode;
            
            String response = sendHttpRequest(stockDailyUrl);
            System.out.println("個股日成交資訊回應:");
            printJsonSummary(response);
            
        } catch (Exception e) {
            System.err.println("獲取個股日成交資訊時發生錯誤:");
            e.printStackTrace();
        }
    }
    
    private static void testMarketInfo() {
        try {
            System.out.println("\n----- 測試大盤資訊 -----");
            
//            LocalDate today = LocalDate.now();
//            String formattedDate = today.format(DateTimeFormatter.ofPattern("yyyyMMdd"));
//            String marketInfoUrl = "https://www.twse.com.tw/exchangeReport/MI_INDEX?response=json&date=" 
//                                + formattedDate + "&type=ALL";

            String marketInfoUrl = "https://www.twse.com.tw/exchangeReport/MI_INDEX";
            String response = sendHttpRequest(marketInfoUrl);
            System.out.println("大盤資訊回應:");
            printJsonSummary(response);
            
        } catch (Exception e) {
            System.err.println("獲取大盤資訊時發生錯誤:");
            e.printStackTrace();
        }
    }
    
    private static void testCompanyInfo(String query) {
        try {
            System.out.println("\n----- 測試公司基本資料查詢 (" + query + ") -----");
            
            String companyInfoUrl = "https://www.twse.com.tw/zh/api/codeQuery?query=" 
                                + java.net.URLEncoder.encode(query, "UTF-8");
            
            String response = sendHttpRequest(companyInfoUrl);
            System.out.println("公司資料查詢回應:");
            System.out.println(response);  // 公司資料查詢回應通常較短，可以完整顯示
            
        } catch (Exception e) {
            System.err.println("查詢公司資料時發生錯誤:");
            e.printStackTrace();
        }
    }
    
    private static String sendHttpRequest(String urlStr) throws Exception {
        @SuppressWarnings("deprecation")
		URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/91.0.4472.124 Safari/537.36");
        conn.setConnectTimeout(10000);
        conn.setReadTimeout(10000);
        
        int responseCode = conn.getResponseCode();
        System.out.println("HTTP Response Code: " + responseCode);
        
        BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
        String inputLine;
        StringBuilder response = new StringBuilder();
        
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();
        
        return response.toString();
    }
    
    // 只打印 JSON 回應的摘要，避免輸出過長
    private static void printJsonSummary(String jsonStr) {
        if (jsonStr == null || jsonStr.isEmpty()) {
            System.out.println("空回應");
            return;
        }
        
        if (jsonStr.length() <= 500) {
            System.out.println(jsonStr);
            return;
        }
        
        // 打印 JSON 開頭部分
        System.out.println(jsonStr.substring(0, 200) + "...");
        System.out.println("(回應過長，只顯示部分內容)");
        System.out.println("總字符數: " + jsonStr.length());
        
        // 檢查是否包含常見字段
        System.out.println("\n包含常見字段檢查:");
        String[] commonFields = {"stat", "date", "title", "fields", "data", "notes", "suggestions"};
        for (String field : commonFields) {
            if (jsonStr.contains("\"" + field + "\"")) {
                System.out.println("- 包含 \"" + field + "\" 字段");
            }
        }
    }
}
