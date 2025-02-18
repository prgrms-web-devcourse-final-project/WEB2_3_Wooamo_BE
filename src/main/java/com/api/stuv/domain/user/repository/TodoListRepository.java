package com.api.stuv.domain.user.repository;

import com.api.stuv.domain.user.entity.TodoList;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoListRepository extends JpaRepository<TodoList, Long> {
}
