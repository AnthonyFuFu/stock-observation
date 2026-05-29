package com.anthonyfu.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

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
