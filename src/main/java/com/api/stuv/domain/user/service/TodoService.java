package com.api.stuv.domain.user.service;

import com.api.stuv.domain.auth.util.TokenUtil;
import com.api.stuv.domain.user.dto.request.AddTodoRequest;
import com.api.stuv.domain.user.dto.request.ModifyTodoRequest;
import com.api.stuv.domain.user.dto.response.AddTodoResponse;
import com.api.stuv.domain.user.dto.response.GetTodoListResponse;
import com.api.stuv.domain.user.entity.TodoList;
import com.api.stuv.domain.user.repository.TodoListRepository;
import com.api.stuv.global.exception.BadRequestException;
import com.api.stuv.global.exception.ErrorCode;
import com.api.stuv.global.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
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

        if(userId == null){
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        TodoList todoList = addTodoRequest.from(addTodoRequest, userId);
        todoListRepository.save(todoList);

        Long todoId = todoList.getId();
        if(todoId == null){
            throw new BadRequestException(ErrorCode.TODO_SAVE_FAILED);
        }

        AddTodoResponse addTodoResponse = new AddTodoResponse(todoId);

        return addTodoResponse;
    }

    public List<GetTodoListResponse> getTodoList(){
        Long userId = tokenUtil.getUserId();
        if(userId == null){
            throw new NotFoundException(ErrorCode.USER_NOT_FOUND);
        }

        List<TodoList> todoList = todoListRepository.getTodoListByUserId(userId);
        if(todoList == null){
            throw new NotFoundException(ErrorCode.TODOLIST_NOT_FOUND);
        }

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

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteAllTodoLists(){
        todoListRepository.deleteAll();
        System.out.println("모든 TodoList가 삭제되었습니다.");
    }

    public void modifyTodoList(Long todoId, ModifyTodoRequest modifyTodoRequest){
        Long userId = tokenUtil.getUserId();

        TodoList todolist = todoListRepository.findTodoListByUserIdAndTodoId(userId, todoId);
        todolist.updateTodoList(modifyTodoRequest);

        todoListRepository.save(todolist);
    }

}
