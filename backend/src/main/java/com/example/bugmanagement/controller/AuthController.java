package com.example.bugmanagement.controller;

import com.example.bugmanagement.common.ApiResponse;
import com.example.bugmanagement.common.CurrentUser;
import com.example.bugmanagement.common.UserContext;
import com.example.bugmanagement.dto.LoginRequest;
import com.example.bugmanagement.dto.LoginResponse;
import com.example.bugmanagement.service.AuthService;
import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ApiResponse<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        return ApiResponse.success(authService.login(request));
    }

    @GetMapping("/me")
    public ApiResponse<CurrentUser> me() {
        return ApiResponse.success(UserContext.get());
    }
}
