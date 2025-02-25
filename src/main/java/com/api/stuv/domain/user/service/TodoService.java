package com.api.stuv.domain.user.service;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.user.dto.request.AddTodoRequest;
import com.api.stuv.domain.user.dto.response.AddTodoResponse;
import com.api.stuv.domain.user.entity.TodoList;
import com.api.stuv.domain.user.repository.TodoListRepository;
import com.api.stuv.domain.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class TodoService {
    private final TokenUtil tokenUtil;
    private final TodoListRepository todoListRepository;

    public AddTodoResponse addTodoList(AddTodoRequest addTodoRequest){
        Long userId = tokenUtil.getUserId();

        TodoList todoList = addTodoRequest.from(addTodoRequest, userId);
        todoListRepository.save(todoList);

        Long todoId = todoList.getId();

        AddTodoResponse addTodoResponse = new AddTodoResponse(todoId);

        return addTodoResponse;
    }
}
