package com.anthonyfu.model.twse;

import java.util.List;

import lombok.Data;

/** TWSE exchangeReport/MI_INDEX 大盤指數 API 回應結構 */
@Data
public class MarketInfo {
    private List<TableData> tables;
    private ParamsData params;
    private String stat;
    private String date;

    @Data
    public static class TableData {
        private String title;
        private List<String> fields;
        private List<List<String>> data;
        private String hints;
        private List<String> notes;
    }

    @Data
    public static class ParamsData {
        private String controller;
        private String action;
        private String lang;
        private String type;
        private String date;
    }
}
