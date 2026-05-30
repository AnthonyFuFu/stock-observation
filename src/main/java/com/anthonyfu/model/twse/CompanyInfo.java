package com.anthonyfu.model.twse;

import java.util.ArrayList;
import java.util.List;

import lombok.Data;

/** TWSE zh/api/codeQuery 公司查詢 API 回應結構 */
@Data
public class CompanyInfo {
    private String query;
    private List<String> suggestions;

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

    @Data
    public static class CompanySuggestion {
        private String code; // 股票代碼
        private String name; // 公司名稱
    }
}
