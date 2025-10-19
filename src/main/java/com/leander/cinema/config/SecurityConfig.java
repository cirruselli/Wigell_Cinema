package com.leander.cinema.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF avstängt för H2
                .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin())) // H2 kräver detta

                .authorizeHttpRequests(auth -> auth
                        // H2-console helt öppet
                        .requestMatchers("/h2-console/**").permitAll()

                        .requestMatchers("/api/v1/customers/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/customers/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/customers/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/movies").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/movies/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/movies").hasAnyRole("USER", "ADMIN") // GET både USER & ADMIN

                        .requestMatchers("/api/v1/rooms/**").hasRole("ADMIN")

                        .requestMatchers("/actuator/**").hasRole("ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/v1/screenings").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/v1/screenings/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/v1/screenings").hasAnyRole("USER", "ADMIN") // GET både USER & ADMIN

                        .requestMatchers(HttpMethod.POST, "/api/v1/bookings/**").hasRole("USER")
                        .requestMatchers(HttpMethod.PATCH, "/api/v1/bookings/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/bookings/**").hasRole("USER")

                        .requestMatchers(HttpMethod.POST, "/api/v1/tickets/**").hasRole("USER")
                        .requestMatchers(HttpMethod.GET, "/api/v1/tickets/**").hasRole("USER")

                        .anyRequest().authenticated()
                )
                .httpBasic(org.springframework.security.config.Customizer.withDefaults());

        return http.build();
    }

    @Bean
    public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
        return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
    }
}