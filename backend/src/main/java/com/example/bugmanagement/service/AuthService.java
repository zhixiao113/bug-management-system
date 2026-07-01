package com.example.bugmanagement.service;

import com.example.bugmanagement.common.AppException;
import com.example.bugmanagement.common.CurrentUser;
import com.example.bugmanagement.dto.LoginRequest;
import com.example.bugmanagement.dto.LoginResponse;
import com.example.bugmanagement.entity.User;
import com.example.bugmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Service
public class AuthService {
    private final UserRepository userRepository;
    private final ConcurrentMap<String, CurrentUser> tokenStore = new ConcurrentHashMap<>();

    public AuthService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new AppException(401, "用户名或密码错误"));
        if (!user.getPassword().equals(request.getPassword())) {
            throw new AppException(401, "用户名或密码错误");
        }

        CurrentUser currentUser = toCurrentUser(user);
        String tokenSource = user.getUsername() + ":" + System.currentTimeMillis() + ":" + UUID.randomUUID();
        String token = Base64.getUrlEncoder().withoutPadding()
                .encodeToString(tokenSource.getBytes(StandardCharsets.UTF_8));
        tokenStore.put(token, currentUser);
        return new LoginResponse(token, currentUser);
    }

    public Optional<CurrentUser> findUserByToken(String token) {
        if (token == null || token.isBlank()) {
            return Optional.empty();
        }
        return Optional.ofNullable(tokenStore.get(token));
    }

    private CurrentUser toCurrentUser(User user) {
        return new CurrentUser(user.getId(), user.getUsername(), user.getNickname(), user.getRole());
    }
}
