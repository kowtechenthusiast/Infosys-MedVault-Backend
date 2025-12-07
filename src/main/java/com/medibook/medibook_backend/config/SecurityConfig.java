package com.medibook.medibook_backend.config;

import com.medibook.medibook_backend.security.JwtAuthFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthFilter jwtAuthFilter;

    public SecurityConfig(JwtAuthFilter jwtAuthFilter) {
        this.jwtAuthFilter = jwtAuthFilter;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                // we use JWT, so disable default CSRF
                .csrf(csrf -> csrf.disable())

                // stateless sessions (no HttpSession)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Configure authorization rules - ORDER MATTERS!
                .authorizeHttpRequests(auth -> auth
                        // Public endpoints FIRST - no authentication required
                        .requestMatchers("/admin/login", "/admin/verify-otp", "/admin/send-otp").permitAll()
                        .requestMatchers("/patient/register", "/patient/login").permitAll()
                        .requestMatchers("/doctor/register", "/doctor/login").permitAll()

                        // Admin-only endpoints - require ADMIN role
                        .requestMatchers("/patient/*/approve", "/patient/*/reject").hasRole("ADMIN")
                        .requestMatchers("/doctor/*/approve", "/doctor/*/reject").hasRole("ADMIN")
                        .requestMatchers("/patient/pending", "/patient/approved", "/patient/rejected", "/patient/all")
                        .hasRole("ADMIN")
                        .requestMatchers("/doctor/pending", "/doctor/approved", "/doctor/rejected", "/doctor/all")
                        .hasRole("ADMIN")

                        // Authenticated users (Patient/Doctor changing password)
                        .requestMatchers("/api/auth/**").authenticated()

                        // All other requests - deny by default
                        .anyRequest().denyAll());

        // Add JWT filter AFTER permitAll checks
        http.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
