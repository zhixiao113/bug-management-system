package com.example.bugmanagement.common;

public final class UserContext {
    private static final ThreadLocal<CurrentUser> CURRENT_USER = new ThreadLocal<>();

    private UserContext() {
    }

    public static void set(CurrentUser user) {
        CURRENT_USER.set(user);
    }

    public static CurrentUser get() {
        CurrentUser user = CURRENT_USER.get();
        if (user == null) {
            throw new AppException(401, "未登录或登录已过期");
        }
        return user;
    }

    public static void clear() {
        CURRENT_USER.remove();
    }
}
