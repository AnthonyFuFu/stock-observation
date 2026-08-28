package com.anthonyfu.model.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("stocks")
public class Stock extends Auditable {

    @Id
    @Column("id")
    private Long id;

    @Column("symbol")
    private String symbol;            // 股票代號 (e.g. 2330, AAPL)

    @Column("name")
    private String name;              // 股票名稱

    @Column("exchange")
    private String exchange;          // 交易所：TWSE / TPEx / NYSE / NASDAQ

    @Column("market_type")
    private String marketType;        // 市場別：TWSE上市 / TPEx上櫃

    @Column("isin_code")
    private String isinCode;          // 國際證券識別碼

    @Column("currency")
    private String currency;          // 幣別（TWD / USD ...）

    @Column("par_value")
    private BigDecimal parValue;      // 面額（元）

    @Column("sector")
    private String sector;            // 產業別

    @Column("industry")
    private String industry;          // 細分產業

    @Column("capital")
    private Long capital;             // 實收資本額（千元）

    @Column("shares_outstanding")
    private Long sharesOutstanding;   // 流通在外股數（股）

    @Column("listed_date")
    private LocalDate listedDate;     // 掛牌日期

    @Column("is_active")
    private Boolean isActive;         // 是否正常交易

    @Column("description")
    private String description;       // 公司簡介
}
