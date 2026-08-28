package com.anthonyfu.model.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;

import lombok.Getter;
import lombok.Setter;

/**
 * 共用稽核欄位（created_at / updated_at）。
 * 由 R2dbcConfig 的 {@code @EnableR2dbcAuditing} 自動填值，新增時寫入 createdAt，
 * 每次儲存時更新 updatedAt，不需在 Service 手動設定。
 * 型別使用 LocalDateTime（而非 ZonedDateTime），因 Spring Data 的稽核機制
 * 僅支援 LocalDateTime/LocalDate/LocalTime/Instant/Date/Long 作為欄位型別。
 */
@Getter
@Setter
public abstract class Auditable {

    @CreatedDate
    @Column("created_at")
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column("updated_at")
    private LocalDateTime updatedAt;
}
