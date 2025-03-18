package projecthub.csv.plugin.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "projecthub.storage.csv")
public class CsvProperties {
    /**
     * Base directory for CSV storage
     */
    private String storageLocation = "data/csv";
    
    /**
     * Whether to create directories if they don't exist
     */
    private boolean createIfMissing = true;
    
    /**
     * Cache configuration
     */
    private Cache cache = new Cache();
    
    @Data
    public static class Cache {
        /**
         * Whether to enable caching
         */
        private boolean enabled = true;
        
        /**
         * Maximum cache size per entity type
         */
        private int maxSize = 1000;
        
        /**
         * Cache expiration in minutes
         */
        private int expirationMinutes = 30;
    }
}
