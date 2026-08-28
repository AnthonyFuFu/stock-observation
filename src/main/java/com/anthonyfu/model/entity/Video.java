package com.anthonyfu.model.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table("videos")
public class Video extends Auditable {

    @Id
    @Column("id")
    private Long id;

    @Column("filename")
    private String filename;          // 對應 resources/videos/ 下的實體檔案

    @Column("title")
    private String title;             // 標題

    @Column("description")
    private String description;       // 簡介

    @Column("category")
    private String category;          // 分類

    @Column("tags")
    private String[] tags;            // 標籤（Postgres TEXT[]）

    @Column("release_date")
    private LocalDate releaseDate;    // 發布日期
}

