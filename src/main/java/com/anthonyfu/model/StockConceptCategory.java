package com.anthonyfu.model;

import java.time.ZonedDateTime;

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
@Table("stock_concept_categories")
public class StockConceptCategory {

    @Id
    private Long id;

    private String code;        // 分類代碼 (AI, NVIDIA, APPLE ...)
    private String name;        // 中文名稱
    private String description;

    @Column("created_at")
    private ZonedDateTime createdAt;
}
