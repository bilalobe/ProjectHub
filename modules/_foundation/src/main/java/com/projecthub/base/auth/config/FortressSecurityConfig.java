package com.projecthub.base.auth.config;

import com.projecthub.base.auth.service.fortress.FortressAuthenticationProvider;
import com.projecthub.base.auth.service.fortress.FortressMethodSecurityExpressionHandler;
import com.projecthub.base.auth.service.fortress.FortressUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.security.access.expression.method.MethodSecurityExpressionHandler;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/**
 * Security configuration specifically for Apache Fortress.
 * This class configures Spring Security to use Fortress components
 * for authentication and authorization.
 */
@Configuration
@EnableMethodSecurity(securedEnabled = true, jsr250Enabled = true)
public class FortressSecurityConfig {

    private final FortressUserDetailsService fortressUserDetailsService;
    private final FortressAuthenticationProvider fortressAuthenticationProvider;
    private final FortressMethodSecurityExpressionHandler expressionHandler;

    public FortressSecurityConfig(
            FortressUserDetailsService fortressUserDetailsService,
            FortressAuthenticationProvider fortressAuthenticationProvider,
            FortressMethodSecurityExpressionHandler expressionHandler) {
        this.fortressUserDetailsService = fortressUserDetailsService;
        this.fortressAuthenticationProvider = fortressAuthenticationProvider;
        this.expressionHandler = expressionHandler;
    }

    /**
     * Configures the primary user details service to use Apache Fortress.
     *
     * @return The Fortress user details service
     */
    @Bean
    @Primary
    public UserDetailsService userDetailsService() {
        return fortressUserDetailsService;
    }

    /**
     * Configures the authentication provider to use Apache Fortress.
     *
     * @return The Fortress authentication provider
     */
    @Bean
    public AuthenticationProvider authenticationProvider() {
        return fortressAuthenticationProvider;
    }

    /**
     * Configures the authentication manager to use our Fortress authentication provider.
     *
     * @param auth the authentication manager builder
     * @return the configured authentication manager
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationManagerBuilder auth) throws Exception {
        auth.authenticationProvider(fortressAuthenticationProvider);
        return auth.build();
    }

    /**
     * Configures the method security expression handler to use our custom Fortress expressions.
     * This enables using expressions like hasObjectPermission() in @PreAuthorize annotations.
     *
     * @return the Fortress method security expression handler
     */
    @Bean
    public MethodSecurityExpressionHandler methodSecurityExpressionHandler() {
        return expressionHandler;
    }

    /**
     * Configures the security filter chain to use our authentication components.
     *
     * @param http the HTTP security configuration
     * @return the configured security filter chain
     */
    @Bean
    public SecurityFilterChain fortressFilterChain(HttpSecurity http) throws Exception {
        http
            .csrf(csrf -> csrf
                .csrfTokenRepository(new CookieCsrfTokenRepository())
            )
            .headers(headers -> headers
                .contentSecurityPolicy(policy -> policy
                    .policyDirectives("default-src 'self'")
                )
            )
            .authorizeHttpRequests(authorize -> authorize
                .requestMatchers("/public/**", "/api/v1/auth/**", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().authenticated()
            )
            .formLogin(Customizer.withDefaults())
            .oauth2Login(Customizer.withDefaults())
            .httpBasic(Customizer.withDefaults())
            .authenticationProvider(fortressAuthenticationProvider);

        return http.build();
    }
}
