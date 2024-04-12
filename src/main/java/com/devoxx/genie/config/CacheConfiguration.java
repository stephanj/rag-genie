package com.devoxx.genie.config;

import io.github.jhipster.config.JHipsterProperties;
import org.ehcache.config.builders.CacheConfigurationBuilder;
import org.ehcache.config.builders.ExpiryPolicyBuilder;
import org.ehcache.config.builders.ResourcePoolsBuilder;
import org.ehcache.jsr107.Eh107Configuration;
import org.hibernate.cache.jcache.ConfigSettings;
import org.springframework.boot.autoconfigure.cache.JCacheManagerCustomizer;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import java.time.Duration;

import static com.devoxx.genie.service.util.CacheNames.USERS_BY_EMAIL;
import static com.devoxx.genie.service.util.CacheNames.USERS_BY_LOGIN;

@Configuration
@EnableCaching
@Profile("!test") // JCACHE / EHCACHE doesn't work well with @SpringBootTest
public class CacheConfiguration {

    protected static final String[] CACHE_NAMES = {
        USERS_BY_EMAIL,
        USERS_BY_LOGIN,
        com.devoxx.genie.domain.Authority.class.getName(),
        com.devoxx.genie.domain.Content.class.getName(),
        com.devoxx.genie.domain.Evaluation.class.getName(),
        com.devoxx.genie.domain.Evaluation.class.getName() + ".user",
        com.devoxx.genie.domain.EvaluationResult.class.getName(),
        com.devoxx.genie.domain.EmbeddingModelReference.class.getName(),
        com.devoxx.genie.domain.Interaction.class.getName(),
        com.devoxx.genie.domain.LanguageModel.class.getName(),
        com.devoxx.genie.domain.User.class.getName(),
        com.devoxx.genie.domain.User.class.getName() + ".authorities",
        com.devoxx.genie.domain.UserAPIKey.class.getName()
    };

    private final javax.cache.configuration.Configuration<Object, Object> jcacheConfiguration;

    public CacheConfiguration(JHipsterProperties jHipsterProperties) {
        JHipsterProperties.Cache.Ehcache ehcache =
            jHipsterProperties.getCache().getEhcache();

        jcacheConfiguration = Eh107Configuration.fromEhcacheCacheConfiguration(
            CacheConfigurationBuilder.newCacheConfigurationBuilder(Object.class, Object.class,
                    ResourcePoolsBuilder.heap(ehcache.getMaxEntries()))
                .withExpiry(ExpiryPolicyBuilder.timeToLiveExpiration(Duration.ofSeconds(ehcache.getTimeToLiveSeconds())))
                .build());
    }

    @Bean
    public HibernatePropertiesCustomizer hibernatePropertiesCustomizer(javax.cache.CacheManager cacheManager) {
        return hibernateProperties -> hibernateProperties.put(ConfigSettings.CACHE_MANAGER, cacheManager);
    }

    @Bean
    public JCacheManagerCustomizer cacheManagerCustomizer() {
        return cm -> {
            for (String cacheName : CACHE_NAMES) {
                createCache(cm, cacheName);
            }
        };
    }

    private void createCache(javax.cache.CacheManager cm, String cacheName) {
        javax.cache.Cache<Object, Object> cache = cm.getCache(cacheName);
        if (cache != null) {
            cm.destroyCache(cacheName);
        }
        cm.createCache(cacheName, jcacheConfiguration);
    }
}
