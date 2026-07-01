package com.example.bugmanagement.controller;

import com.example.bugmanagement.common.ApiResponse;
import com.example.bugmanagement.common.UserContext;
import com.example.bugmanagement.entity.User;
import com.example.bugmanagement.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping
    public ApiResponse<List<User>> list() {
        return ApiResponse.success(userService.findAll(UserContext.get()));
    }
}
