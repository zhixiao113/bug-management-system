package com.example.bugmanagement.service;

import com.example.bugmanagement.common.AppException;
import com.example.bugmanagement.common.CurrentUser;
import com.example.bugmanagement.entity.User;
import com.example.bugmanagement.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public List<User> findAll(CurrentUser currentUser) {
        if (!currentUser.isAdmin()) {
            throw new AppException(403, "只有管理员可以查看用户列表");
        }
        return userRepository.findAll().stream()
                .peek(user -> user.setPassword(null))
                .toList();
    }
}
