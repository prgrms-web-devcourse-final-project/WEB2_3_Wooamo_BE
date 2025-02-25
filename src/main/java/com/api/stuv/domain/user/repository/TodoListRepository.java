package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.user.dto.response.GetTodoListResponse;
import com.api.stuv.domain.user.entity.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TodoListRepository extends JpaRepository<TodoList, Long> {

    @Query("SELECT t FROM TodoList t WHERE t.userId = :userId")
    List<TodoList> getTodoListByUserId(Long userId);
}
