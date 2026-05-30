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
import java.time.ZonedDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("stocks")
public class Stock {
    @Id
    private Long id;

    private String symbol;
    private String name;
    private String exchange;

    @Column("market_type")
    private String marketType;        // TWSE=上市 / TPEx=上櫃

    @Column("isin_code")
    private String isinCode;          // 國際證券識別碼

    private String currency;          // 幣別（TWD / USD ...）

    @Column("par_value")
    private BigDecimal parValue;      // 面額（元）

    private String sector;
    private String industry;

    private Long capital;             // 實收資本額（千元）

    @Column("shares_outstanding")
    private Long sharesOutstanding;   // 流通在外股數（股）

    @Column("listed_date")
    private LocalDate listedDate;

    @Column("is_active")
    private Boolean isActive;

    private String description;

    @Column("created_at")
    private ZonedDateTime createdAt;

    @Column("updated_at")
    private ZonedDateTime updatedAt;
}
