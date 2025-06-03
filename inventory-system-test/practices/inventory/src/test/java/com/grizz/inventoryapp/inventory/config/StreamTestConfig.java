package com.grizz.inventoryapp.inventory.config;

import org.springframework.boot.test.context.TestConfiguration;

/**
 * Spring Cloud Stream 4.x에서 spring-cloud-stream-test-binder는
 * 테스트 binder를 자동 주입하지 않음
 * @TestConfiguration으로 직접 테스트용 binder 설정을 등록해줘야만
 * OutputDestination bean이 등록됨
 * 아래 적용 후 Could not autowire. No beans of 'OutputDestination' type found. 에러 해결
 */
@TestConfiguration
public class StreamTestConfig {
}
