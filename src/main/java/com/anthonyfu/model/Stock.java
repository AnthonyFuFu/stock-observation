package com.anthonyfu.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

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
    private String sector;
    private String industry;
    
    @Column("listed_date")
    private LocalDate listedDate;
    private String description;
    
    @Column("created_at")
    private ZonedDateTime createdAt;
    
    @Column("updated_at")
    private ZonedDateTime updatedAt;
}