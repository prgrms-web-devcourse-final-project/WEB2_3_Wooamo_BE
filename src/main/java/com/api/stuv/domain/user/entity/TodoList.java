package com.api.stuv.domain.user.entity;

import com.api.stuv.domain.user.dto.request.ModifyTodoRequest;
import com.api.stuv.global.entity.BaseTimeEntity;
import lombok.*;
import jakarta.persistence.*;

@Getter
@Entity
@Table(name = "todo_list")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TodoList extends BaseTimeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long userId;

    @Column(nullable = false)
    private String todo;

    @Column(nullable = false)
    private Boolean isChecked;

    @Builder
    public TodoList(Long userId, String todo, Boolean isChecked) {
        this.userId = userId;
        this.todo = todo;
        this.isChecked = isChecked;
    }

    public void updateTodoList(ModifyTodoRequest modifyTodoRequest){
        this.todo = modifyTodoRequest.todo();
        this.isChecked = modifyTodoRequest.isChecked();
    }
}