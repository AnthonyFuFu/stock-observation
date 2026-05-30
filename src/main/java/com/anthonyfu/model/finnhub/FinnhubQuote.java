package com.anthonyfu.model.finnhub;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

/** Finnhub /quote 即時報價 API 回應結構 */
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinnhubQuote {
    private Double c;   // current price
    private Double d;   // change
    private Double dp;  // percent change
    private Double h;   // high of day
    private Double l;   // low of day
    private Double o;   // open of day
    private Double pc;  // previous close
    private Long t;     // timestamp (Unix seconds)
}
