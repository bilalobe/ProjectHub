package projecthub.csv.plugin.config;

import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

@Configuration
@EnableCaching
@Profile("csv")
public class CsvCacheConfig {

    public CsvCacheConfig() {
    }

    @Bean
    public CacheManager cacheManager() {
        return new ConcurrentMapCacheManager("csvEntities");
    }
}
