package com.anthonyfu.model;

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
    private Long id;
    
    @Column("stock_id")
    private Long stockId;
    
    @Column("trade_date")
    private LocalDate tradeDate;
    
    @Column("open_price")
    private BigDecimal openPrice;
    
    @Column("high_price")
    private BigDecimal highPrice;
    
    @Column("low_price")
    private BigDecimal lowPrice;
    
    @Column("close_price")
    private BigDecimal closePrice;
    
    @Column("adjusted_close")
    private BigDecimal adjustedClose;

    @Column("change_price")
    private BigDecimal changePrice;   // 漲跌金額

    private Long volume;              // 成交股數

    private Long turnover;            // 成交金額（元）

    private Integer transactions;     // 成交筆數
}