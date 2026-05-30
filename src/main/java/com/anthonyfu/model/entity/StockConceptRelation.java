package com.anthonyfu.model.entity;

import java.math.BigDecimal;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("stock_concept_relations")
public class StockConceptRelation {

    @Id
    private Long id;

    @Column("stock_id")
    private Long stockId;

    @Column("category_id")
    private Long categoryId;

    @Builder.Default
    private BigDecimal weight = BigDecimal.ONE;  // 關聯權重（1.0 = 主要概念股）
}
