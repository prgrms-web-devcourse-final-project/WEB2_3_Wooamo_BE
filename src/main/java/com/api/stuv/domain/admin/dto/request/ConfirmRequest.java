package com.api.stuv.domain.admin.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ConfirmRequest(
        LocalDate date,
        Boolean auth
) {
}
