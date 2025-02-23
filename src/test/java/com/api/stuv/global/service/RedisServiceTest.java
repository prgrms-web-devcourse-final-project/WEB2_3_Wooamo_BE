package com.api.stuv.global.service;

import com.api.stuv.global.config.RedisConfig;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Import(RedisConfig.class) // Redis 설정 파일 import
class RedisServiceTest {

    private static final Logger log = LoggerFactory.getLogger(RedisServiceTest.class);
    @Autowired
    private RedisService redisService;

    @Test
    @DisplayName("Redis에 String 값을 저장하고 조회할 수 있어야 한다.")
    void testSaveAndFind_StringValue() {
        // GIVEN
        String key = "stringKey";
        String value = "Hello Redis";
        Duration timeout = Duration.ofMinutes(10);

        // WHEN
        redisService.save(key, value, timeout);
        String result = redisService.find(key, String.class);

        // THEN
        assertThat(result).isEqualTo(value);

        redisService.delete(key);
    }

    @Test
    @DisplayName("Redis에 Object 값을 저장하고 조회할 수 있어야 한다.")
    void testSaveAndFind_ObjectValue() {
        // GIVEN
        String key = "objectKey";
        TestObject expected = new TestObject("testData", 10);

        // WHEN
        redisService.save(key, expected, Duration.ofMinutes(10));
        TestObject result = redisService.find(key, TestObject.class);

        log.info("result: {}", result);

        // THEN
        assertThat(result).isNotNull();
        assertThat(result.getData()).isEqualTo(expected.getData());

        redisService.delete(key);
    }

    @Test
    @DisplayName("존재하지 않는 키를 조회하면 null을 반환해야 한다.")
    void testFind_NonExistingKey() {
        // WHEN
        Object result = redisService.find("nonExistingKey", Object.class);

        // THEN
        assertThat(result).isNull();
    }

    @Test
    @DisplayName("Redis에서 데이터를 삭제하면 더 이상 조회되지 않아야 한다.")
    void testDelete() {
        // GIVEN
        String key = "deleteKey";
        redisService.save(key, "toBeDeleted", Duration.ofMinutes(10));

        // WHEN
        redisService.delete(key);
        Object result = redisService.find(key, Object.class);

        // THEN
        assertThat(result).isNull();
    }

    // 테스트용 객체
    @Getter
    @NoArgsConstructor
    @AllArgsConstructor
    private static class TestObject {
        private String data;
        private Integer number;
    }
}
