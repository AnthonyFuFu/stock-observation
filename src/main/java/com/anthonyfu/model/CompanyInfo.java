package com.anthonyfu.model;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

@Data
public class CompanyInfo {
    private String query;
    private List<String> suggestions;
    
    // 輔助方法：獲取解析後的建議列表
    public List<CompanySuggestion> getParsedSuggestions() {
        List<CompanySuggestion> result = new ArrayList<>();
        
        if (suggestions != null) {
            for (String suggestion : suggestions) {
                String[] parts = suggestion.split("\t");
                if (parts.length >= 2) {
                    CompanySuggestion cs = new CompanySuggestion();
                    cs.setCode(parts[0]);
                    cs.setName(parts[1]);
                    result.add(cs);
                }
            }
        }
        
        return result;
    }
    
    // 內部類：表示一個公司建議
    @Data
    public static class CompanySuggestion {
        private String code; // 股票代碼
        private String name; // 公司名稱
    }
}