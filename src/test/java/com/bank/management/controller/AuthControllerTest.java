package com.bank.management.controller;

import com.bank.management.dto.RegisterRequest;
import com.bank.management.dto.RegisterResponse;
import com.bank.management.service.AuthService;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import com.bank.management.service.JwtService;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.bank.management.dto.LoginRequest;
import com.bank.management.dto.LoginResponse;

import com.bank.management.controller.AuthController;

@WebMvcTest(AuthController.class)
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthService authService;

    @MockitoBean
    private JwtService jwtService;

    @Test
    void shouldRegisterUser() throws Exception {

        RegisterResponse response =
                new RegisterResponse(
                        1L,
                        "anas",
                        "USER"
                );

        when(authService.register(any(RegisterRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                        {
                                            "username": "anas",
                                            "password": "password123"
                                        }
                                        """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                        {
                            "id": 1,
                            "username": "anas",
                            "role": "USER"
                        }
                        """));
    }

    @Test
    void shouldLoginUser() throws Exception {

        LoginResponse response =
                new LoginResponse(
                        "Login successful",
                        "anas",
                        "USER",
                        "test-token"
                );

        when(authService.login(any(LoginRequest.class)))
                .thenReturn(response);

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                                    {
                                        "username": "anas",
                                        "password": "password123"
                                    }
                                    """)
                )
                .andExpect(status().isOk())
                .andExpect(content().json("""
                    {
                        "message": "Login successful",
                        "username": "anas",
                        "role": "USER",
                        "token": "test-token"
                    }
                    """));
    }

    @Test
    void shouldRejectRegisterWhenUsernameIsEmpty() throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "username": "",
                                "password": "password123"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectRegisterWhenPasswordIsEmpty() throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "username": "anas",
                                "password": ""
                            }
                            """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectRegisterWhenPasswordIsTooShort() throws Exception {

        mockMvc.perform(
                        post("/api/auth/register")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "username": "anas",
                                "password": "12345"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectLoginWhenUsernameIsEmpty() throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "username": "",
                                "password": "password123"
                            }
                            """)
                )
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldRejectLoginWhenPasswordIsEmpty() throws Exception {

        mockMvc.perform(
                        post("/api/auth/login")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content("""
                            {
                                "username": "anas",
                                "password": ""
                            }
                            """)
                )
                .andExpect(status().isBadRequest());
    }
}