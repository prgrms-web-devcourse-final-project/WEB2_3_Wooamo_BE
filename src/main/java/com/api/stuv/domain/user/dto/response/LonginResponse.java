package com.api.stuv.domain.user.dto.response;


import com.api.stuv.domain.user.entity.User;

public record LonginResponse(
        String username,
        String password
) {
    public static LonginResponse from(User user) {
        return new LonginResponse (user.getEmail(), user.getPassword());
    }
}
