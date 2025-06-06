package com.grizz.inventoryapp.inventory.repository.redis;

import com.grizz.inventoryapp.test.exception.NotImplementedTestException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.redis.DataRedisTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.GenericContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import static org.junit.jupiter.api.Assertions.*;

@Tag("integration")
@Import(InventoryRedisRepositoryImpl.class)
@Testcontainers
@DataRedisTest
public class InventoryRedisRepositoryTest {

    @Container
    private static final GenericContainer<?> redisContainer;

    static {
        redisContainer = new GenericContainer<>("redis:7.2.0")
                .withExposedPorts(6379);
        redisContainer.start();
    }

    @DynamicPropertySource
    static void setDatasourceProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.data.redis.port", () -> redisContainer.getMappedPort(6379));
    }

    @Autowired
    InventoryRedisRepository sut;

    @Autowired
    StringRedisTemplate redisTemplate;

    final String existingItemId = "1";
    final String nonExistingItemId = "2";
    final Long stock = 100L;

    @BeforeEach
    void setUp() {
        redisTemplate.opsForValue().set(sut.key(existingItemId), stock.toString());
    }

    @AfterEach
    void tearDown() {
        if (redisTemplate.getConnectionFactory() != null) {
            redisTemplate.getConnectionFactory().getConnection().serverCommands().flushAll(); // 테스트 환경에서 Redis 데이터 초기화
        }
    }

    @Nested
    class GetStock {
        @DisplayName("itemId를 갖는 inventory가 없다면, null을 반환한다")
        @Test
        void test1() {
            // given
            final Long result = sut.getStock(nonExistingItemId);

            // when
            assertNull(result);
        }

        @DisplayName("itemId를 갖는 inventory가 있다면, stock을 반환한다")
        @Test
        void test2() {
            // given
            final Long result = sut.getStock(existingItemId);

            // when
            assertEquals(stock, result);
        }
    }

    @Nested
    class DecreaseStock {
        final Long quantity = 10L;

        @DisplayName("itemId를 갖는 inventory가 없다면, inventory를 생성하고 stock을 차감하고 반환한다")
        @Test
        void test1() {
            // when
            final Long result = sut.decreaseStock(nonExistingItemId, quantity);

            // then
            assertEquals(-quantity, result);
        }

        @DisplayName("itemId를 갖는 inventory가 있다면, stock을 차감하고 반환한다")
        @Test
        void test2() {
            // when
            final Long result = sut.decreaseStock(existingItemId, quantity);

            // then
            assertEquals(stock - quantity, result);
        }
    }

    @Nested
    class DeleteStock {
        @DisplayName("itemId를 갖는 inventory가 없다면, false를 반환한다")
        @Test
        void test1() {
            // when
            final Boolean result = sut.deleteStock(nonExistingItemId);

            // then
            assertFalse(result);
        }

        @DisplayName("itemId를 갖는 inventory가 있다면, stock을 삭제하고 true를 반환한다")
        @Test
        void test2() {
            // when
            final Boolean result = sut.deleteStock(existingItemId);

            // then
            assertTrue(result);

            final Long keySize = redisTemplate.getConnectionFactory().getConnection().serverCommands().dbSize();
            assertEquals(0, keySize);
        }
    }

    @Nested
    class SetStock {
        final Long newStock = 200L;

        @DisplayName("itemId를 갖는 inventory가 없다면, inventory를 생성하고 stock을 수정하고 반환한다")
        @Test
        void test1() {
            // when
            final Long result = sut.setStock(nonExistingItemId, newStock);

            // then
            assertEquals(newStock, result);
        }

        @DisplayName("itemId를 갖는 inventory가 있다면, stock을 수정하고 반환한다")
        @Test
        void test2() {
            // when
            final Long result = sut.setStock(existingItemId, newStock);

            // then
            assertEquals(newStock, result);
        }
    }

}
