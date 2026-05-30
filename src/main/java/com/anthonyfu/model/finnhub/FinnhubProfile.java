package com.anthonyfu.model.finnhub;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/** Finnhub /stock/profile2 公司基本資料 API 回應結構 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinnhubProfile {
    private String name;
    private String ticker;
    private String exchange;
    private String country;
    private String currency;
    private String finnhubIndustry;
    private String logo;
    private String weburl;
    private Double marketCapitalization;
}
