package com.anthonyfu.model;

import java.util.List;
import lombok.Data;

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