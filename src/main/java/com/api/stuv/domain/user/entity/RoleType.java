package com.api.stuv.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    USER("회원"),
    ADMIN("관리자");

    private final String text;

    public static RoleType fromText(String text) {
        for (RoleType role : values()) {
            if (role.getText().equals(text)) {
                return role;
            }
        }
        throw new IllegalArgumentException("Unexpected text: " + text);
    }
}
