package com.api.stuv.domain.user.entity;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum RoleType {
    USER("회원"),
    ADMIN("관리자");

    private final String text;
}
