@Configuration
@EnableJpaRepositories(
    basePackages = "com.projecthub.skills.repository",
    entityManagerFactoryRef = "skillsEntityManagerFactory"
)
public class SkillsModuleConfig {
    public SkillsModuleConfig() {
    }

    @Bean
    @ConfigurationProperties(prefix = "skills.datasource")
    public DataSource skillsDataSource() {
        return DataSourceBuilder.create().build();
    }
}
