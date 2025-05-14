package com.grizz.inventoryapp.inventory.integration;

import com.grizz.inventoryapp.controller.consts.ErrorCodes;
import com.grizz.inventoryapp.test.exception.NotImplementedTestException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ActiveProfiles("integration-test") // test 용 컨테이너 사용
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE) // 테스트 환경에서 h2 데이터베이스를 자동으로 사용하지 않도록 설정
@Sql(
        scripts = {"classpath:schema.sql", "classpath:data.sql"}, // 순서 중요
        executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD // 테스트 실행 전 스키마와 데이터를 로드
)
@AutoConfigureMockMvc
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class InventoryIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

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

        mockMvc.perform(
                        post("/api/v1/inventory/{itemId}/decrease", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.item_id").value(existingItemId))
                .andExpect(jsonPath("$.data.stock").value(90L));

        // 3. 재고를 조회하고 90개인 것을 확인한다.
        successGetStock(existingItemId, 90L);
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
        final Long quantity = 1000L;
        final String requestBody = String.format("{\"stock\": %d}", quantity);

        mockMvc.perform(
                        patch("/api/v1/inventory/{itemId}/stock", existingItemId)
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.item_id").value(existingItemId))
                .andExpect(jsonPath("$.data.stock").value(1000L));

        // 3. 재고를 조회하고 100개인 것을 확인한다.
        successGetStock(existingItemId, 1000L);
    }

    @DisplayName("재고 차감, 수정 종합")
    @Test
    void test7() {
        throw new NotImplementedTestException();
    }

    private void successGetStock(String itemId, Long stock) throws Exception {
        mockMvc.perform(get("/api/v1/inventory/{itemId}", itemId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.data.item_id").value(itemId))
                .andExpect(jsonPath("$.data.stock").value(stock));
    }

}
