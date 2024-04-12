package com.devoxx.genie.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;

import static com.devoxx.genie.config.CacheConfiguration.CACHE_NAMES;

@TestConfiguration
@EnableCaching
public class CacheTestConfig {

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager(CACHE_NAMES);
    }

}
