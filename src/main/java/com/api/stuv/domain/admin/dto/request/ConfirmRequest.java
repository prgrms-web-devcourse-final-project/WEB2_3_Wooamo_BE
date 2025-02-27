package com.api.stuv.domain.admin.dto.request;

import jakarta.validation.constraints.NotNull;

import java.time.LocalDate;

public record ConfirmRequest(
        @NotNull LocalDate date,
        @NotNull Boolean auth
) {
}
