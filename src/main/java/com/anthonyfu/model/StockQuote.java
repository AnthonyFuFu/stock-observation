package com.anthonyfu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("stock_quotes")
public class StockQuote {
    @Id
    private Long id;
    
    @Column("stock_id")
    private Long stockId;
    
    @Column("current_price")
    private BigDecimal currentPrice;
    
    @Column("price_change")
    private BigDecimal priceChange;
    
    @Column("change_percent")
    private BigDecimal changePercent;
    
    private Long volume;
    
    @Column("last_updated")
    private ZonedDateTime lastUpdated;
}