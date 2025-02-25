package com.api.stuv.domain.user.dto.request;

import com.api.stuv.domain.user.entity.TodoList;

public record AddTodoRequest(
        String todo
) {
    public static TodoList from(AddTodoRequest addTodoRequest, Long userId){
        return TodoList.builder()
                .userId(userId)
                .todo(addTodoRequest.todo())
                .isChecked(false)
                .build();
    }
}
