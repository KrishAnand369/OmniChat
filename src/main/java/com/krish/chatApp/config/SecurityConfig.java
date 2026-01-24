package com.krish.chatApp.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. ENABLE CORS (This was missing!)
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))

                // 2. Disable CSRF (Not needed for stateless APIs)
                .csrf(AbstractHttpConfigurer::disable)

                // 3. Stateless Session (No cookies)
                .sessionManagement(session ->
                        session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // 4. Endpoint Rules
                .authorizeHttpRequests(auth -> auth
                        // Allow Login & Health checks openly
                        .requestMatchers("/api/v1/auth/**", "/actuator/**").permitAll()

                        // Allow the WebSocket Handshake endpoint (We secure it inside the socket config)
                        .requestMatchers("/ws-chat/**").permitAll()

                        // Lock everything else
                        .anyRequest().authenticated()
                );

        // 4. Add the Filter (We will create this next)
        // http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    // --- DEFINE THE CORS RULES ---
    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();

        // Allow ALL origins (including your file:// and localhost:3000)
        configuration.setAllowedOriginPatterns(List.of("*"));

        // Allow ALL HTTP methods (GET, POST, OPTIONS, etc.)
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));

        // Allow ALL headers
        configuration.setAllowedHeaders(List.of("*"));

        // Allow Credentials (Cookies/Auth headers)
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}