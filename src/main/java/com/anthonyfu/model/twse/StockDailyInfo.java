package com.anthonyfu.model.twse;

import java.util.List;
import lombok.Data;

/** TWSE exchangeReport/STOCK_DAY 及 TAIEX API 回應結構 */
@Data
public class StockDailyInfo {
    private String stat;
    private String date;
    private String title;
    private List<String> fields;
    private List<List<String>> data;
    private List<String> notes;
    private String total;
}
