package com.anthonyfu.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class CandlePoint {
    private Long timestamp;  // Unix seconds
    private Double open;
    private Double high;
    private Double low;
    private Double close;
    private Double volume;
}
