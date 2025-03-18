package projecthub.csv.plugin.config;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import projecthub.csv.plugin.service.CsvMigrationService;

@AutoConfiguration
@EnableConfigurationProperties(CsvProperties.class)
@ComponentScan(basePackages = {
    "projecthub.csv.plugin.helper",
    "projecthub.csv.plugin.impl",
    "projecthub.csv.plugin.service"
})
@Import({CsvCacheConfig.class})
@ConditionalOnProperty(prefix = "projecthub.storage", name = "type", havingValue = "csv")
public class CsvConfig {

    public CsvConfig() {
    }

    @Bean
    public CsvInitializer csvInitializer(CsvMigrationService migrationService) {
        return new CsvInitializer(migrationService);
    }

    private static class CsvInitializer {
        public CsvInitializer(CsvMigrationService migrationService) {
            // Perform schema migration during initialization
            migrationService.migrateIfNeeded();
        }
    }
}
