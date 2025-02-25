package com.api.stuv.domain.user.dto.response;

public record GetTodoListResponse(
        Long todoId,
        String todo,
        Boolean isChecked
) {
}
