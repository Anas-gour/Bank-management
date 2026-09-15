package com.bank.management.config;

import com.bank.management.service.JwtService;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;

import org.springframework.stereotype.Component;

import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;


@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;


    // =========================================================
    // CONSTRUCTOR
    // =========================================================

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }


    // =========================================================
    // JWT FILTER
    // =========================================================

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {

        System.out.println();
        System.out.println("==========================================");
        System.out.println(
                "JWT FILTER REQUEST: "
                        + request.getMethod()
                        + " "
                        + request.getRequestURI()
        );
        System.out.println("==========================================");


        // =====================================================
        // GET AUTHORIZATION HEADER
        // =====================================================

        String authHeader = request.getHeader("Authorization");


        // =====================================================
        // CHECK AUTHORIZATION HEADER
        // =====================================================

        if (authHeader == null ||
                !authHeader.startsWith("Bearer ")) {

            System.out.println("NO AUTHORIZATION HEADER");

            filterChain.doFilter(request, response);

            return;
        }


        System.out.println("AUTH HEADER PRESENT");


        // =====================================================
        // EXTRACT TOKEN
        // =====================================================

        String token = authHeader.substring(7);


        try {

            // =================================================
            // EXTRACT USERNAME
            // =================================================

            String username =
                    jwtService.extractUsername(token);

            System.out.println(
                    "JWT USERNAME: " + username
            );


            // =================================================
            // EXTRACT ROLE
            // =================================================

            String role =
                    jwtService.extractRole(token);

            System.out.println(
                    "JWT ROLE: " + role
            );


            // =================================================
            // CHECK USERNAME AND ROLE
            // =================================================

            if (username != null && role != null) {

                // =============================================
                // CREATE AUTHORITY
                // =============================================

                String authority;

                if (role.startsWith("ROLE_")) {
                    authority = role;
                } else {
                    authority = "ROLE_" + role;
                }

                System.out.println(
                        "AUTHORITY: " + authority
                );


                // =============================================
                // CREATE AUTHENTICATION
                // =============================================

                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(
                                username,
                                null,
                                List.of(
                                        new SimpleGrantedAuthority(
                                                authority
                                        )
                                )
                        );


                // =============================================
                // SET SECURITY CONTEXT
                // =============================================

                SecurityContextHolder
                        .getContext()
                        .setAuthentication(authentication);


                System.out.println(
                        "AUTHENTICATION SET SUCCESSFULLY"
                );


                // =============================================
                // DEBUG
                // =============================================

                System.out.println(
                        "AUTHENTICATION OBJECT: "
                                + SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                );

                System.out.println(
                        "AUTHORITIES: "
                                + SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getAuthorities()
                );
            }


            // =================================================
            // FINAL SECURITY CONTEXT CHECK
            // =================================================

            System.out.println(
                    "FINAL AUTH BEFORE CONTROLLER: "
                            + SecurityContextHolder
                            .getContext()
                            .getAuthentication()
            );


            if (SecurityContextHolder
                    .getContext()
                    .getAuthentication() != null) {

                System.out.println(
                        "FINAL AUTHORITIES BEFORE CONTROLLER: "
                                + SecurityContextHolder
                                .getContext()
                                .getAuthentication()
                                .getAuthorities()
                );

            } else {

                System.out.println(
                        "FINAL AUTHORITIES BEFORE CONTROLLER: NULL"
                );
            }


            // =================================================
            // CONTINUE REQUEST
            // =================================================

            filterChain.doFilter(request, response);


        } catch (Exception e) {

            // =================================================
            // INVALID / EXPIRED JWT
            // =================================================

            System.out.println(
                    "JWT AUTHENTICATION FAILED"
            );

            System.out.println(
                    "ERROR: " + e.getMessage()
            );


            response.setStatus(
                    HttpServletResponse.SC_UNAUTHORIZED
            );

            response.setContentType(
                    "application/json"
            );

            response.getWriter().write(
                    "{\"message\":\"Invalid or expired token\"}"
            );
        }
    }
}