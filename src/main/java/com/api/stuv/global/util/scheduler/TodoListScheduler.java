package com.api.stuv.global.util.scheduler;

import com.api.stuv.domain.user.repository.TodoListRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class TodoListScheduler {
    private final TodoListRepository todoListRepository;

    @Scheduled(cron = "0 0 0 * * *")
    public void deleteAllTodoLists(){
        todoListRepository.deleteAll();
        log.info("== 전날 TODO-LIST 삭제 완료 (MariaDB: delete) ==");
    }
}
