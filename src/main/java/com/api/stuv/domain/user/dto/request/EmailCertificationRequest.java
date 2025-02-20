package com.api.stuv.domain.user.dto.request;

public record EmailCertificationRequest(
        String email,
        String code
) {
}
