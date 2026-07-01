package com.example.bugmanagement.dto;

import com.example.bugmanagement.common.CurrentUser;

public record LoginResponse(String token, CurrentUser user) {
}
