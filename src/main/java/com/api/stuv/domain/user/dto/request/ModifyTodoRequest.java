package com.api.stuv.domain.user.dto.request;

public record ModifyTodoRequest(
        String todo,
        Boolean isChecked
) {
}
