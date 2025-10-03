package com.kevin.soccertracker.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // authorize rules
                .authorizeHttpRequests(auth -> auth
                        // allow Swagger/OpenAPI
                        .requestMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**").permitAll()
                        // allow ALL API endpoints (dev-friendly)
                        .requestMatchers("/api/**").permitAll()
                        // anything else can be locked down later
                        .anyRequest().permitAll()
                )
                // disable CSRF for stateless API testing (Scratch HTTP, curl, etc.)
                .csrf(csrf -> csrf.disable());

        // If you later want basic auth, add: .httpBasic(Customizer.withDefaults());
        return http.build();
    }
}
