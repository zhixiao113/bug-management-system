package com.example.bugmanagement.common;

public record CurrentUser(Long id, String username, String nickname, String role) {
    public boolean isAdmin() {
        return "ADMIN".equals(role);
    }

    public boolean isTester() {
        return "TESTER".equals(role);
    }

    public boolean isDeveloper() {
        return "DEVELOPER".equals(role);
    }
}
