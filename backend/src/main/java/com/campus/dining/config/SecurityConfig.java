package com.campus.dining.config;

import com.campus.dining.security.JwtAuthenticationFilter;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.util.Map;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .csrf(AbstractHttpConfigurer::disable)
            .sessionManagement(session ->
                session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                // 公开接口
                .requestMatchers("/api/auth/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/stalls/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/dishes/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/dish-categories/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/announcements/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/activities/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()
                .requestMatchers(HttpMethod.GET, "/api/stats/**").permitAll()
                // 管理员接口
                .requestMatchers("/api/admin/**").hasRole("ADMIN")
                // 商户接口
                .requestMatchers("/api/merchant/**").hasAnyRole("MERCHANT", "ADMIN")
                // 其他接口需要认证
                .anyRequest().authenticated()
            )
            .exceptionHandling(ex -> ex
                .authenticationEntryPoint((request, response, authException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(401);
                    new ObjectMapper().writeValue(response.getOutputStream(),
                            Map.of("code", 401, "message", "未登录或登录已过期"));
                })
                .accessDeniedHandler((request, response, accessDeniedException) -> {
                    response.setContentType(MediaType.APPLICATION_JSON_VALUE);
                    response.setCharacterEncoding("UTF-8");
                    response.setStatus(403);
                    new ObjectMapper().writeValue(response.getOutputStream(),
                            Map.of("code", 403, "message", "没有权限访问"));
                })
            )
            .addFilterBefore(jwtAuthenticationFilter,
                    UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
