# ProjectHub CSV Plugin

A plugin for ProjectHub that provides CSV import/export functionality for project data, evaluations, and team assignments.

## Features

- **Data Import**: Import projects, teams, and evaluations from CSV files
- **Data Export**: Export data to standardized CSV formats
- **Template Support**: Configurable CSV templates for different data types
- **Validation**: Data validation and error reporting
- **Batch Processing**: Efficient handling of large datasets

## Installation

Add to your module's build.gradle:
```gradle
dependencies {
    implementation project(':modules:plugins:projecthub-csv-plugin')
}
```

## Usage

### Configuration

```yaml
projecthub:
  plugins:
    csv:
      enabled: true
      template-path: classpath:/templates/csv
      charset: UTF-8
      delimiter: ','
      batch-size: 1000
```

### CSV Templates

Default templates are provided for:
- Projects (`project_template.csv`)
- Teams (`team_template.csv`)
- Evaluations (`evaluation_template.csv`)

### Example Usage

```java
@Autowired
private CSVDataService csvService;

// Import projects
csvService.importProjects("projects.csv");

// Export evaluations
csvService.exportEvaluations("evaluations.csv");
```

## Development Guide

### Plugin Structure

```
projecthub-csv-plugin/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/projecthub/plugins/csv/
│   │   │       ├── config/      # Plugin configuration
│   │   │       ├── service/     # CSV processing services
│   │   │       └── validation/  # Data validation
│   │   └── resources/
│   │       └── templates/       # CSV templates
│   └── test/                    # Unit tests
```

### Implementing New Features

1. Create a new service class:
```java
@Service
public class CustomCSVProcessor {
    // Implementation
}
```

1. Register in plugin configuration:
```java
@Configuration
public class CSVPluginConfig {
    @Bean
    public CustomCSVProcessor customProcessor() {
        return new CustomCSVProcessor();
    }
}
```

## Testing

```bash
./gradlew :modules:plugins:projecthub-csv-plugin:test
```

## Error Handling

The plugin provides detailed error reporting:
- Validation errors
- Format issues
- Data consistency problems

## Best Practices

1. **Data Validation**
   - Always validate CSV structure
   - Check data consistency
   - Handle character encoding

2. **Performance**
   - Use batch processing
   - Implement proper error handling
   - Monitor memory usage

## Contributing

1. Fork the plugin repository
2. Create a feature branch
3. Submit a pull request

## Support

- Check [troubleshooting guide](../../docs/plugins/csv-troubleshooting.md)
- Report issues on GitHub
- Join developer discussions
