package com.leander.cinema.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/h2-console/**").permitAll() // Tillåt H2
                        .anyRequest().authenticated()
                )
                .csrf(csrf -> csrf.disable()) // H2 kräver att CSRF är av
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())); // Modern ersättning

        return http.build();
    }
}
