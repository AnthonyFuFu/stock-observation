package com.anthonyfu.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

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
