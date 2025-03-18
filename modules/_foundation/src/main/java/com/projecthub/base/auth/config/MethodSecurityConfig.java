@Configuration
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class MethodSecurityConfig {
    public MethodSecurityConfig() {
    }

    @Bean
    public static MethodSecurityExpressionHandler expressionHandler() {
        return new FortressMethodSecurityExpressionHandler();
    }
}
