package com.anthonyfu.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("stock_prices")
public class StockPrice {

    @Id
    @Column("id")
    private Long id;

    @Column("stock_id")
    private Long stockId;             // 對應 stocks.id

    @Column("trade_date")
    private LocalDate tradeDate;      // 交易日期

    @Column("open_price")
    private BigDecimal openPrice;     // 開盤價

    @Column("high_price")
    private BigDecimal highPrice;     // 最高價

    @Column("low_price")
    private BigDecimal lowPrice;      // 最低價

    @Column("close_price")
    private BigDecimal closePrice;    // 收盤價

    @Column("adjusted_close")
    private BigDecimal adjustedClose; // 還原價

    @Column("change_price")
    private BigDecimal changePrice;   // 漲跌金額

    @Column("volume")
    private Long volume;              // 成交股數

    @Column("turnover")
    private Long turnover;            // 成交金額（元）

    @Column("transactions")
    private Integer transactions;     // 成交筆數
}
