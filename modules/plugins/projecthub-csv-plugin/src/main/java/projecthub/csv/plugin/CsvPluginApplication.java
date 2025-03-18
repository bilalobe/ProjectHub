package projecthub.csv.plugin;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.ComponentScan;
import projecthub.csv.plugin.config.CsvProperties;

@SpringBootApplication
@ComponentScan(basePackages = {
    "projecthub.csv.plugin.repository",
    "projecthub.csv.plugin.config",
    "projecthub.csv.plugin.helper"
})
@EnableConfigurationProperties(CsvProperties.class)
public class CsvPluginApplication {

    public CsvPluginApplication() {
    }

    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(CsvPluginApplication.class, args);
    }
}
