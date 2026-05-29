package com.anthonyfu.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class FinnhubCandle {
    private List<Double> c;  // close prices
    private List<Double> h;  // high prices
    private List<Double> l;  // low prices
    private List<Double> o;  // open prices
    private String s;         // status: "ok" or "no_data"
    private List<Long> t;    // timestamps (Unix seconds)
    private List<Double> v;  // volumes
}
