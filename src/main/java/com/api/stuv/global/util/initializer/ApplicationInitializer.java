package com.api.stuv.global.util.initializer;

import com.api.stuv.domain.timer.service.TimerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ApplicationInitializer implements ApplicationListener<ApplicationReadyEvent> {
    private final TimerService timerService;

    @Override
    public void onApplicationEvent(ApplicationReadyEvent event) {
        log.info("== 서버 시작: 초기 랭킹 데이터 갱신 중 ==");
        timerService.updateWeeklyRanking();
        log.info("== 초기 랭킹 데이터 갱신 완료 ==");
    }
}
