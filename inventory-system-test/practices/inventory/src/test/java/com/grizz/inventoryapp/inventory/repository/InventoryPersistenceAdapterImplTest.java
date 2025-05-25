package com.grizz.inventoryapp.inventory.repository;

import com.grizz.inventoryapp.inventory.repository.jpa.InventoryJpaRepositoryStub;
import com.grizz.inventoryapp.inventory.repository.redis.InventoryRedisRepositoryStub;
import com.grizz.inventoryapp.inventory.service.domain.Inventory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class InventoryPersistenceAdapterImplTest {

    @InjectMocks
    InventoryPersistenceAdapterImpl sut;

    @Spy
    InventoryJpaRepositoryStub inventoryJpaRepositoryStub;

    @Spy
    InventoryRedisRepositoryStub inventoryRedisRepositoryStub;

    @Nested
    class FindByItemId {
        final String existingItemId = "1";
        final String nonExistingItemId = "2";
        final Long stock = 100L;

        @BeforeEach
        void setUp() {
            // Assuming there is a method to add inventory for testing
            inventoryJpaRepositoryStub.addInventoryEntity(existingItemId, stock);
            inventoryRedisRepositoryStub.addStock(existingItemId, stock);
        }

        @DisplayName("itemId를 갖는 entity가 없다면, null을 반환한다")
        @Test
        void test1() {
            // when
            final Inventory result = sut.findByItemId(nonExistingItemId);

            // then
            assertNull(result);
        }

        @DisplayName("itemId를 갖는 entity가 있다면, inventory를 반환한다")
        @Test
        void test2() {
            // when
            final Inventory result = sut.findByItemId(existingItemId);

            // then
            assertNotNull(result);

            assertEquals(existingItemId, result.getItemId());
            assertEquals(stock, result.getStock());
        }
    }

    @Nested
    class DecreaseByItemId {
        final String existingItemId = "1";
        final String nonExistingItemId = "2";
        final Long stock = 100L;

        @BeforeEach
        void setUp() {
            // Assuming there is a method to add inventory for testing
            inventoryJpaRepositoryStub.addInventoryEntity(existingItemId, stock);
            inventoryRedisRepositoryStub.addStock(existingItemId, stock);
        }

        @DisplayName("itemId를 갖는 entity가 없다면, null을 반환한다")
        @Test
        void test1() {
            // when
            final Inventory result = sut.decreaseStock(nonExistingItemId, 1L);

            // then
            assertNull(result);
        }

        @DisplayName("itemId를 갖는 entity가 있다면, stock을 차감하고 inventory를 반환한다")
        @Test
        void test2() {
            // when
            final Inventory result = sut.decreaseStock(existingItemId, 1L);

            // then
            assertNotNull(result);

            long expectedStock = stock - 1;
            assertEquals(expectedStock, result.getStock());
        }
    }

    @Nested
    class Save {
        final String existingItemId = "1";
        final String nonExistingItemId = "2";
        final Long oldStock = 100L;

        @BeforeEach
        void setUp() {
            // Assuming there is a method to add inventory for testing
            inventoryJpaRepositoryStub.addInventoryEntity(existingItemId, oldStock);
            inventoryRedisRepositoryStub.addStock(existingItemId, oldStock);
        }

        @DisplayName("id를 갖는 entity가 없다면, entity를 추가하고 추가된 inventory를 반환한다")
        @Test
        void test1() {
            // given
            final Long newStock = 1234L;

            // when
            final Inventory inventory = new Inventory(null, nonExistingItemId, newStock);
            final Inventory result = sut.save(inventory);

            // then
            assertNotNull(result);
            assertEquals(nonExistingItemId, result.getItemId());
            assertEquals(newStock, result.getStock());
        }

        @DisplayName("id를 갖는 entity가 있다면, entity를 수정하고 수정된 inventory를 반환한다")
        @Test
        void test2() {
            // given
            final Long newStock = 1234L;
            final Inventory inventory = sut.findByItemId(existingItemId);

            // when
            inventory.setStock(newStock);
            final Inventory result = sut.save(inventory);

            // then
            assertEquals(1L, result.getId());
            assertEquals(existingItemId, result.getItemId());
            assertEquals(newStock, result.getStock());
        }
    }

}