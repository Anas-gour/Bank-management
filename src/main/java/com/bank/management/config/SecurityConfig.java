package com.bank.management.config;

import jakarta.servlet.http.HttpServletResponse;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import org.springframework.http.HttpMethod;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;


@Configuration
public class SecurityConfig {

    // =========================================================
    // PASSWORD ENCODER
    // =========================================================

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }


    // =========================================================
    // JWT FILTER
    // =========================================================

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }


    // =========================================================
    // IMPORTANT:
    // Disable Spring Boot's automatic servlet registration
    // of JwtAuthenticationFilter.
    //
    // We will register it ONLY inside Spring Security
    // using addFilterBefore().
    // =========================================================

    @Bean
    public FilterRegistrationBean<JwtAuthenticationFilter>
    jwtAuthenticationFilterRegistration(
            JwtAuthenticationFilter filter) {

        FilterRegistrationBean<JwtAuthenticationFilter> registration =
                new FilterRegistrationBean<>();

        registration.setFilter(filter);

        registration.setEnabled(false);

        return registration;
    }


    // =========================================================
    // SECURITY FILTER CHAIN
    // =========================================================

    @Bean
    public SecurityFilterChain securityFilterChain(
            HttpSecurity http) throws Exception {

        http

                // =====================================================
                // CSRF
                // =====================================================

                .csrf(csrf -> csrf.disable())


                // =====================================================
                // AUTHORIZATION
                // =====================================================

                .authorizeHttpRequests(auth -> auth

                        // -------------------------------------------------
                        // PUBLIC APIs
                        // -------------------------------------------------

                        .requestMatchers(
                                "/api/auth/register",
                                "/api/auth/login",

                                "/api/v1/docs/**",
                                "/v3/api-docs/**",
                                "/swagger-ui/**",
                                "/api/v1/swagger-ui/**"
                        )
                        .permitAll()


                        // -------------------------------------------------
                        // CUSTOMER
                        // -------------------------------------------------

                        // ADMIN only
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/customers"
                        )
                        .hasRole("ADMIN")


                        // ADMIN only
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/customers/**"
                        )
                        .hasRole("ADMIN")


                        // ADMIN only
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/customers/**"
                        )
                        .hasRole("ADMIN")


                        // USER + ADMIN
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/customers/**"
                        )
                        .hasAnyRole("USER", "ADMIN")


                        // -------------------------------------------------
                        // ACCOUNT
                        // -------------------------------------------------

                        // ADMIN only
                        .requestMatchers(
                                HttpMethod.POST,
                                "/api/accounts"
                        )
                        .hasRole("ADMIN")


                        // ADMIN only
                        .requestMatchers(
                                HttpMethod.PUT,
                                "/api/accounts/**"
                        )
                        .hasRole("ADMIN")


                        // ADMIN only
                        .requestMatchers(
                                HttpMethod.DELETE,
                                "/api/accounts/**"
                        )
                        .hasRole("ADMIN")


                        // USER + ADMIN
                        .requestMatchers(
                                HttpMethod.GET,
                                "/api/accounts/**"
                        )
                        .hasAnyRole("USER", "ADMIN")


                        // -------------------------------------------------
                        // TRANSACTIONS
                        // -------------------------------------------------

                        .requestMatchers(
                                "/api/transactions/**"
                        )
                        .hasAnyRole("USER", "ADMIN")


                        // -------------------------------------------------
                        // EVERYTHING ELSE
                        // -------------------------------------------------

                        .anyRequest()
                        .authenticated()
                )


                // =====================================================
                // EXCEPTION HANDLING
                // =====================================================

                .exceptionHandling(exception -> exception

                        // -------------------------------------------------
                        // 401
                        // Not authenticated
                        // -------------------------------------------------

                        .authenticationEntryPoint(
                                (request, response, authException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_UNAUTHORIZED
                                    );

                                    response.setContentType(
                                            "application/json"
                                    );

                                    response.getWriter().write(
                                            "{\"message\":\"Authentication required\"}"
                                    );
                                }
                        )


                        // -------------------------------------------------
                        // 403
                        // Authenticated but insufficient permission
                        // -------------------------------------------------

                        .accessDeniedHandler(
                                (request, response, accessDeniedException) -> {

                                    response.setStatus(
                                            HttpServletResponse.SC_FORBIDDEN
                                    );

                                    response.setContentType(
                                            "application/json"
                                    );

                                    response.getWriter().write(
                                            "{\"message\":\"Access denied\"}"
                                    );
                                }
                        )
                )


                // =====================================================
                // JWT FILTER
                // =====================================================

                .addFilterBefore(
                        jwtAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class
                );


        return http.build();
    }
}