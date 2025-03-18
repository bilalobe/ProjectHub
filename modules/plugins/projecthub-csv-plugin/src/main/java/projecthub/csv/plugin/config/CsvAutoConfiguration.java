package projecthub.csv.plugin.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@AutoConfiguration
@EnableConfigurationProperties(CsvProperties.class)
@ConditionalOnProperty(prefix = "csv.storage", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import({CsvConfig.class, CsvCacheConfig.class})
@ComponentScan(basePackages = "projecthub.csv.plugin")
public class CsvAutoConfiguration {
    public CsvAutoConfiguration() {
    }
    // Auto-configuration is handled by component scanning and imports
}
