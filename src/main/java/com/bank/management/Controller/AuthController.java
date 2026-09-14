package com.bank.management.Controller;

import com.bank.management.dto.RegisterRequest;
import com.bank.management.dto.RegisterResponse;
import com.bank.management.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.*;

import com.bank.management.dto.LoginRequest;
import com.bank.management.dto.LoginResponse;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public RegisterResponse register(
            @Valid @RequestBody RegisterRequest request) {

        return authService.register(request);
    }

    @PostMapping("/login")
    public LoginResponse login(
            @Valid @RequestBody LoginRequest request) {

        return authService.login(request);
    }
}