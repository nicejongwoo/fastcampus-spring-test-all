package com.grizz.inventoryapp.inventory.integration;

import com.grizz.inventoryapp.inventory.config.StreamTestConfig;
import com.grizz.inventoryapp.inventory.controller.consts.ErrorCodes;
import com.grizz.inventoryapp.inventory.repository.redis.InventoryRedisRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.stream.binder.test.OutputDestination;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.messaging.Message;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static com.grizz.inventoryapp.test.assertion.Assertions.assertDecreasedEventEquals;
import static com.grizz.inventoryapp.test.assertion.Assertions.assertUpdatedEventEquals;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@Transactional
@ActiveProfiles("integration-test2") // test 용 컨테이너 사용
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트 환경에서 h2 데이터베이스를 자동으로 사용하지 않도록 설정
@Sql(
        scripts = {"classpath:schema.sql", "classpath:data.sql"}, // 순서 중요
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_CLASS // 테스트 실행 전 스키마와 데이터를 로드
)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ImportAutoConfiguration(StreamTestConfig.class)
public class AnnotationInventoryIntegrationTest {

    @Container
    private static final MySQLContainer<?> mySQLContainer = new MySQLContainer<>("mysql:8.3")
            .withDatabaseName("inventory")
            .withUsername("root")
            .withPassword("root")
            .withUrlParam("serverTimezone", "UTC")
            .withUrlParam("useSSL", "false")
            .withUrlParam("allowPublicKeyRetrieval", "true");

    @Container
    private static final GenericContainer<?> redisContainer = new GenericContainer<>("redis:7.2")
            .withExposedPorts(6379);

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", mySQLContainer::getJdbcUrl);
        registry.add("spring.datasource.username", mySQLContainer::getUsername);
        registry.add("spring.datasource.password", mySQLContainer::getPassword);
        registry.add("srpgin.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private StringRedisTemplate redisTemplate;

    @Autowired
    private InventoryRedisRepository inventoryRedisRepository;

    @Autowired
    private OutputDestination outputDestination;

    @BeforeEach
    void setUp() {
        redisTemplate.opsForValue().set(inventoryRedisRepository.key(existingItemId), stock.toString());
    }

    @AfterEach
    void tearDown() {
        redisTemplate.getConnectionFactory().getConnection().flushAll();
    }

    final String existingItemId = "1";
    final String nonExistingItemId = "2";
    final Long stock = 100L;

    @DisplayName("재고 조회 실패")
    @Test
    void test1() throws Exception {
        // given
        mockMvc.perform(get("/api/v1/inventory/{itemId}", nonExistingItemId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error.code").value(ErrorCodes.ITEM_NOT_FOUND.code))
                .andExpect(jsonPath("$.error.local_message").value(ErrorCodes.ITEM_NOT_FOUND.message));
    }

    @DisplayName("재고 조회 성공")
    @Test
    void test2() throws Exception {
        successGetStock(existingItemId, stock);
    }

    @DisplayName("재고 차감 실패")
    @Test
    void test3() throws Exception {
        // 1. 재고를 조회하고 100개인 것을 확인한다.
        successGetStock(existingItemId, stock);

        // 2. 재고 110개를 차감하고 실패한다.
        final Long quantity = 110L;
        final String requestBody = String.format("{\"quantity\": %d}", quantity);

        mockMvc.perform(
                        post("/api/v1/inventory/{itemId}/decrease", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(ErrorCodes.INSUFFICIENT_STOCK.code))
                .andExpect(jsonPath("$.error.local_message").value(ErrorCodes.INSUFFICIENT_STOCK.message));

        // 3. 재고를 조회하고 100개인 것을 확인한다.
        successGetStock(existingItemId, stock);
    }

    @DisplayName("재고 차감 성공")
    @Test
    void test4() throws Exception {
        // 1. 재고를 조회하고 100개인 것을 확인한다.
        successGetStock(existingItemId, stock);

        // 2. 재고 10개를 차감하고 실패한다.
        final Long quantity = 10L;
        final String requestBody = String.format("{\"quantity\": %d}", quantity);

        long expectedStock = 90L;
        mockMvc.perform(
                        post("/api/v1/inventory/{itemId}/decrease", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.item_id").value(existingItemId))
                .andExpect(jsonPath("$.data.stock").value(expectedStock));

        // 3. 재고를 조회하고 90개인 것을 확인한다.
        successGetStock(existingItemId, expectedStock);

        // 4. 재고 차감 이벤트 한 번 발행된 것을 확인한다.
        final Message<byte[]> result = outputDestination.receive(1000, "inventory-out-0");
        assertDecreasedEventEquals(result, existingItemId, quantity, expectedStock);
    }

    @DisplayName("재고 수정 실패")
    @Test
    void test5() throws Exception {
        // 1. 재고를 조회하고 100개인 것을 확인한다.
        successGetStock(existingItemId, stock);

        // 2. 재고 -100개를 차감하고 실패한다.
        final Long quantity = -100L;
        final String requestBody = String.format("{\"stock\": %d}", quantity);

        mockMvc.perform(
                        patch("/api/v1/inventory/{itemId}/stock", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error.code").value(ErrorCodes.INVALID_STOCK.code))
                .andExpect(jsonPath("$.error.local_message").value(ErrorCodes.INVALID_STOCK.message));

        // 3. 재고를 조회하고 100개인 것을 확인한다.
        successGetStock(existingItemId, 100L);
    }

    @DisplayName("재고 수정 성공")
    @Test
    void test6() throws Exception {
        // 1. 재고를 조회하고 100개인 것을 확인한다.
        successGetStock(existingItemId, stock);

        // 2. 재고 1000개를 차감하고 실패한다.
        long newStock = 1000L;
        final Long quantity = newStock;
        final String requestBody = String.format("{\"stock\": %d}", quantity);

        mockMvc.perform(
                        patch("/api/v1/inventory/{itemId}/stock", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.item_id").value(existingItemId))
                .andExpect(jsonPath("$.data.stock").value(newStock));

        // 3. 재고를 조회하고 100개인 것을 확인한다.
        successGetStock(existingItemId, newStock);

        // 4. 재고 수정 이벤트 1번 발행된 것을 확인한다.
        final Message<byte[]> result = outputDestination.receive(1000, "inventory-out-0");
        assertUpdatedEventEquals(result, existingItemId, newStock);
    }

    @DisplayName("재고 차감, 수정 종합")
    @Test
    void test7() throws Exception {
        // 1. 재고를 조회하고 100개인 것을 확인한다.
        long expectedStock = 100L;
        successGetStock(existingItemId, expectedStock);

        // 2. 재고를 10개 차감을 7번 반복하고 성공한다.
        final Long quantity = 10L;
        for (int i = 0; i < 7; i++) {
            expectedStock -= quantity;
            final String requestBody = String.format("{\"quantity\": %d}", quantity);
            mockMvc.perform(
                            post("/api/v1/inventory/{itemId}/decrease", existingItemId)
                                    .contentType(MediaType.APPLICATION_JSON)
                                    .content(requestBody))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.data.item_id").value(existingItemId))
                    .andExpect(jsonPath("$.data.stock").value(expectedStock));
        }

        // 3. 재고를 조회하고 30개인 것을 확인한다.
        successGetStock(existingItemId, 30L);

        // 4. 재고를 500개로 수정하고 성공한다.
        long newStock = 500L;
        final String requestBody = String.format("{\"stock\": %d}", newStock);
        mockMvc.perform(
                        patch("/api/v1/inventory/{itemId}/stock", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.item_id").value(existingItemId))
                .andExpect(jsonPath("$.data.stock").value(newStock));

        // 5. 재고를 조회하고 500개인 것을 확인한다.
        successGetStock(existingItemId, newStock);

        // 6. 재고 차감 이벤트 7번, 재고 수정 이벤트 1번 발행된 것을 확인한다.
        Long preStock = 100L;
        for (int i = 0; i < 7; i++) {
            preStock -= quantity;
            final Message<byte[]> result = outputDestination.receive(1000, "inventory-out-0");
            assertDecreasedEventEquals(result, existingItemId, quantity, preStock);
        }
    }

    private void successGetStock(String itemId, Long stock) throws Exception {
        mockMvc.perform(get("/api/v1/inventory/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.item_id").value(itemId))
                .andExpect(jsonPath("$.data.stock").value(stock));
    }

}
