package com.api.stuv.domain.user.service;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.user.dto.request.AddTodoRequest;
import com.api.stuv.domain.user.dto.request.ModifyTodoRequest;
import com.api.stuv.domain.user.dto.response.AddTodoResponse;
import com.api.stuv.domain.user.dto.response.GetTodoListResponse;
import com.api.stuv.domain.user.entity.TodoList;
import com.api.stuv.domain.user.repository.TodoListRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

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

    public List<GetTodoListResponse> getTodoList(){
        Long userId = tokenUtil.getUserId();

        List<TodoList> todoList = todoListRepository.getTodoListByUserId(userId);

        List<GetTodoListResponse> todoListResponses = todoListRepository.getTodoListByUserId(userId)
                .stream()
                .map(todo -> new GetTodoListResponse(todo.getId(), todo.getTodo(), todo.getIsChecked()))
                .toList();

        return todoListResponses;
    }

    public void deleteTodoList(Long todoId){
        Long userId = tokenUtil.getUserId();

        TodoList todolist = todoListRepository.findTodoListByUserIdAndTodoId(userId, todoId);

        todoListRepository.delete(todolist);
    }

    public void modifyTodoList(Long todoId, ModifyTodoRequest modifyTodoRequest){
        Long userId = tokenUtil.getUserId();

        TodoList todolist = todoListRepository.findTodoListByUserIdAndTodoId(userId, todoId);
        todolist.updateTodoList(modifyTodoRequest);

        todoListRepository.save(todolist);
    }

}
