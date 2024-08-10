package com.sascom.stockpricebackend.global.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Configuration
public class AppConfig {

    @Bean
    public Map<String, String> companyCodeMap() {
        return new ConcurrentHashMap<>();
    }
}
