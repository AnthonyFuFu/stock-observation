package com.anthonyfu.config;

import java.time.Duration;

import javax.net.ssl.SSLException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.WebClient;

import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.InsecureTrustManagerFactory;
import reactor.netty.http.client.HttpClient;

@Configuration
public class WebClientConfig {
	
    @Bean
    WebClient webClient() throws SSLException {
        SslContext sslContext = SslContextBuilder.forClient()
                .trustManager(InsecureTrustManagerFactory.INSTANCE)
                .build();

        HttpClient httpClient = HttpClient.create()
                .secure(spec -> spec.sslContext(sslContext))
                .responseTimeout(Duration.ofSeconds(60))        // 回應超時 60 秒
                .option(io.netty.channel.ChannelOption.CONNECT_TIMEOUT_MILLIS, 30000);  // 連線超時 30 秒

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .codecs(config -> config.defaultCodecs()
                        .maxInMemorySize(10 * 1024 * 1024))  // 10MB buffer
                .build();
    }
}