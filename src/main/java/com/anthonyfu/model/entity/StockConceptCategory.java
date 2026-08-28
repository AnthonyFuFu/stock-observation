package com.anthonyfu.model.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
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
    @Column("id")
    private Long id;

    @Column("code")
    private String code;              // 分類代碼 (AI, NVIDIA, APPLE ...)

    @Column("name")
    private String name;              // 中文名稱

    @Column("description")
    private String description;       // 分類說明

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;  // 建立時間
}
