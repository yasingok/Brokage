package com.example.Brokage.config;

import com.example.Brokage.enums.Role;
import com.example.Brokage.service.CustomUserDetailsService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfig {
    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);
    private final CustomUserDetailsService userDetailsService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> {csrf.disable();
                     csrf.ignoringRequestMatchers("/h2-console/**");
                })
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/h2-console/**","/customers/login").permitAll()
                        .requestMatchers("/admin/match-orders/**").hasAnyRole(Role.ADMIN.name())
                        .requestMatchers("/orders/**").hasAnyRole(Role.ADMIN.name(), Role.CUSTOMER.name())
                        .requestMatchers("/assets/**").hasAnyRole(Role.ADMIN.name(), Role.CUSTOMER.name())
                )
                .headers(headers -> headers.frameOptions().sameOrigin())
                .httpBasic(customizer -> customizer
                .authenticationEntryPoint((request, response, authException) -> {
                    response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                })
        );
        return http.build();
    }

    @Bean
    public AuthenticationManager authManager(HttpSecurity http) throws Exception {
        AuthenticationManagerBuilder authenticationManagerBuilder =
                http.getSharedObject(AuthenticationManagerBuilder.class);
        authenticationManagerBuilder.userDetailsService(userDetailsService)
                .passwordEncoder(passwordEncoder());
        return authenticationManagerBuilder.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
