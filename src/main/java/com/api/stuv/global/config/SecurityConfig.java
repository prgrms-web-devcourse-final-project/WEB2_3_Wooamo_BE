package com.api.stuv.global.config;

import com.api.stuv.domain.auth.jwt.CustomLogoutFilter;
import com.api.stuv.domain.auth.jwt.JWTFilter;
import com.api.stuv.domain.auth.jwt.JWTUtil;
import com.api.stuv.domain.auth.jwt.LoginFilter;
import com.api.stuv.global.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.authentication.logout.LogoutFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.Arrays;

@Configuration
@RequiredArgsConstructor
@EnableWebSecurity
public class SecurityConfig {
    private final AuthenticationConfiguration authenticationConfiguration;
    private final JWTUtil jwtUtil;

    @Value("${frontend.server}")
    private String frontInstance;

    @Value("${ip}")
    private String ip1;

    //복호화
    @Bean
    public BCryptPasswordEncoder bCryptPasswordEncoder() {
        return new BCryptPasswordEncoder();
    }

    //AuthenticationManager Bean 등록
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(Arrays.asList("http://localhost:3000", frontInstance, "http://"+ip1));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        configuration.setAllowedHeaders(Arrays.asList("Authorization", "Cache-Control", "Content-Type", "*"));
        configuration.setExposedHeaders(Arrays.asList("Authorization", "Set-Cookie", "access"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, RedisService redisService) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((auth) -> auth
                .requestMatchers("/login", "/", "/join").permitAll()
                .requestMatchers("/admin").hasRole("ADMIN")
                .requestMatchers("/reissue").permitAll()
                .anyRequest().permitAll())
                .addFilterBefore(new JWTFilter(jwtUtil), LoginFilter.class)
                .addFilterAt(new LoginFilter(authenticationManager(authenticationConfiguration), jwtUtil, redisService), UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new CustomLogoutFilter(jwtUtil, redisService), LogoutFilter.class)
                .sessionManagement((session) -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .build();
    }
}