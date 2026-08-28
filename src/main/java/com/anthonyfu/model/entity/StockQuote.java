package com.anthonyfu.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("stock_quotes")
public class StockQuote {

    @Id
    @Column("id")
    private Long id;

    @Column("stock_id")
    private Long stockId;             // 對應 stocks.id

    @Column("open_price")
    private BigDecimal openPrice;     // 開盤價

    @Column("high_price")
    private BigDecimal highPrice;     // 最高價

    @Column("low_price")
    private BigDecimal lowPrice;      // 最低價

    @Column("current_price")
    private BigDecimal currentPrice;  // 現價

    @Column("prev_close")
    private BigDecimal prevClose;     // 昨日收盤

    @Column("price_change")
    private BigDecimal priceChange;   // 漲跌金額

    @Column("change_percent")
    private BigDecimal changePercent; // 漲跌幅（%）

    @Column("volume")
    private Long volume;              // 成交股數

    @Column("turnover")
    private Long turnover;            // 成交金額（元）

    @LastModifiedDate
    @Column("last_updated")
    private LocalDateTime lastUpdated; // 最後更新時間
}
