package projecthub.csv.plugin.config;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Profile;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

@TestConfiguration
@Profile("test")
public class TestConfig {

    public TestConfig() {
    }

    @Bean
    public CsvProperties csvProperties() {
        CsvProperties properties = new CsvProperties();
        properties.setBasePath("test-data/csv");
        return properties;
    }

    @Bean
    public jakarta.validation.Validator validator() {
        return new LocalValidatorFactoryBean();
    }
}
