package com.anthonyfu.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.r2dbc.config.EnableR2dbcAuditing;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

@Configuration
@EnableR2dbcRepositories
@EnableR2dbcAuditing
public class R2dbcConfig {
    // 稽核欄位改用 LocalDateTime 後，DB 的 TIMESTAMPTZ 與實體型別可原生對應，
    // 不再需要 ZonedDateTime 的自訂 Converter。
}