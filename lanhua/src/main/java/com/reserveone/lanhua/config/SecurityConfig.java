package com.reserveone.lanhua.config;

import com.reserveone.lanhua.security.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod; // Asegúrate de importar esto
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Autowired
    private JwtFilter jwtFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .cors(cors -> cors.configure(http))
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**", "/swagger-ui.html").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/users").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/users/**").permitAll()
                        .requestMatchers("/api/user-information", "/api/user-information/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/schedules/**").permitAll()
                        .requestMatchers(HttpMethod.PUT, "/api/user-information").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/schedules/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/memberships/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/schedule/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/catalog/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/catalog/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/catalog/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/memberships/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/memberships/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/memberships/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/schedules/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/schedules/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/schedules/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/reservations").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/users").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/users/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.GET, "/api/subscriptions").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.POST, "/api/subscriptions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.PUT, "/api/subscriptions/**").hasRole("ADMIN")
                        .requestMatchers(HttpMethod.DELETE, "/api/subscriptions/**").hasRole("ADMIN")
                        .requestMatchers("/api/payments/**").authenticated()
                        .requestMatchers("/api/reservations/**").authenticated()
                        .anyRequest().authenticated()
                );

       http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        org.springframework.web.cors.CorsConfiguration configuration = new org.springframework.web.cors.CorsConfiguration();
        configuration.setAllowedOrigins(java.util.Arrays.asList("http://localhost:3000", "http://localhost:5173", "http://localhost:4200", "http://127.0.0.1:5502", "http://localhost:5502"));
        configuration.setAllowedMethods(java.util.Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(java.util.Arrays.asList("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);

        org.springframework.web.cors.UrlBasedCorsConfigurationSource source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        return source;
    }
}