
package com.quizchamp;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http
            .securityContext(context -> context
                .securityContextRepository(
                    new HttpSessionSecurityContextRepository()
                )
            )
            .sessionManagement(session -> session
                .sessionCreationPolicy(
                    SessionCreationPolicy.IF_REQUIRED
                )
            )
            .csrf(csrf -> csrf.disable())
            .authorizeHttpRequests(auth -> auth

                // Public pages
                .requestMatchers(
                    "/",
                    "/index.html",
                    "/admin.html",
                    "/student.html",
                    "/leaderboard.html",
                    "/favicon.ico",
                    "/css/**",
                    "/js/**",
                    "/images/**"
                ).permitAll()
                 
                
                // Public registration, login, and session check
               .requestMatchers(
                   "/api/auth/register",
                   "/api/auth/login",
                  "/api/auth/session-check"
                ).permitAll()



                // Admin APIs remain admin-only
                .requestMatchers("/api/admin/**")
                    .hasRole("ADMIN")

               .requestMatchers("/api/student/**").authenticated()
                // All other APIs require authentication

                .requestMatchers("/api/**")
                    .authenticated()

                .anyRequest().permitAll()
            )
            .formLogin(form -> form.disable())
            .httpBasic(basic -> basic.disable());

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration configuration)
            throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}




