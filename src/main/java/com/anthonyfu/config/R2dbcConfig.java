package com.anthonyfu.config;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.ReadingConverter;
import org.springframework.data.convert.WritingConverter;
import org.springframework.data.r2dbc.convert.R2dbcCustomConversions;
import org.springframework.data.r2dbc.dialect.DialectResolver;
import org.springframework.data.r2dbc.dialect.R2dbcDialect;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;

import io.r2dbc.spi.ConnectionFactory;

@Configuration
@EnableR2dbcRepositories
public class R2dbcConfig {
    
	@Bean
	R2dbcCustomConversions r2dbcCustomConversions(ConnectionFactory connectionFactory) {
	    R2dbcDialect dialect = DialectResolver.getDialect(connectionFactory);
	    List<Converter<?, ?>> converters = new ArrayList<>();
	    converters.add(new ZonedDateTimeReadConverter());
	    converters.add(new ZonedDateTimeWriteConverter());
	    return R2dbcCustomConversions.of(dialect, converters);
	}

    @ReadingConverter
    public static class ZonedDateTimeReadConverter implements Converter<java.time.LocalDateTime, ZonedDateTime> {
        @Override
        public ZonedDateTime convert(java.time.LocalDateTime source) {
            return source.atZone(ZoneId.systemDefault());
        }
    }

    @WritingConverter
    public static class ZonedDateTimeWriteConverter implements Converter<ZonedDateTime, java.time.LocalDateTime> {
        @Override
        public java.time.LocalDateTime convert(ZonedDateTime source) {
            return source.toLocalDateTime();
        }
    }
}