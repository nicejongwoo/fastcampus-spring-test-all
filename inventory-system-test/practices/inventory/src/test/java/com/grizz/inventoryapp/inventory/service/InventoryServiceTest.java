package com.grizz.inventoryapp.inventory.service;

import com.grizz.inventoryapp.inventory.service.domain.Inventory;
import com.grizz.inventoryapp.inventory.service.exception.InsufficientStockException;
import com.grizz.inventoryapp.inventory.service.exception.InvalidDecreaseQuantityException;
import com.grizz.inventoryapp.inventory.service.exception.InvalidStockException;
import com.grizz.inventoryapp.inventory.service.exception.ItemNotFoundException;
import com.grizz.inventoryapp.inventory.service.persistence.InventoryPersistenceAdapterStub;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
public class InventoryServiceTest {

    @InjectMocks
    InventoryService sut; // system under test

    @Spy
    InventoryPersistenceAdapterStub inventoryAdapter;

    @Nested
    class FindByItemId {
        final String existentItemId = "1";
        final Long stock = 10L;

        @BeforeEach
        void setUp() {
            inventoryAdapter.addInventory(existentItemId, stock);
        }

        @DisplayName("itemId를 갖는 entity를 찾지 못하면, null을 반환한다.")
        @Test
        void test1() {
            // given
            final String nonExistentItemId = "2";

            // when
            final Inventory result = sut.findByItemId(nonExistentItemId);

            // then
            assertNull(result);
        }

        @DisplayName("itemId를 갖는 entity를 찾으면, inventory를 반환한다.")
        @Test
        void test1000() {
            // given
            // when
            final Inventory result = sut.findByItemId(existentItemId);

            // then
            assertNotNull(result);
            assertEquals(existentItemId, result.getItemId());
            assertEquals(stock, result.getStock());
        }

    }

    @Nested
    class DecreaseByItemId {
        final String existingItemId = "1";
        final Long stock = 10L;

        @BeforeEach
        void setUp() {
            inventoryAdapter.addInventory(existingItemId, stock);
        }

        @DisplayName("quantity가 음수라면, Exception을 throw한다")
        @Test
        void test1() {
            // given
            final Long quantity = -1L;

            // when & then
            assertThrows(InvalidDecreaseQuantityException.class, () -> {
                sut.decreaseByItemId(existingItemId, quantity);
            });
        }

        @DisplayName("itemId를 갖는 entity를 찾지 못하면, Exception을 throw한다")
        @Test
        void test2() {
            // given
            final String nonExistingItemId = "2";
            final Long quantity = 10L;

            // when & then
            assertThrows(ItemNotFoundException.class, () -> {
                sut.decreaseByItemId(nonExistingItemId, quantity);
            });
        }

        @DisplayName("quantity가 stock보다 크면, Exception을 throw한다")
        @Test
        void test3() {
            // given
            final Long quantity = stock + 1;

            // when & then
            assertThrows(InsufficientStockException.class, () -> {
                sut.decreaseByItemId(existingItemId, stock + 1);
            });
        }


        @DisplayName("변경된 entity가 없다면, Exception을 throw한다")
        @Test
        void test4() {
            // given
            final Long quantity = 10L;

            // 업데이트되는 최종 결과가 무조건 0을 반환하도록 stubbing(mocking) 한다.(업데이트가 안되는 결과를 가정)
            doReturn(0).when(inventoryAdapter).decreaseStock(existingItemId, quantity);

            // when & then
            assertThrows(ItemNotFoundException.class, () -> {
                sut.decreaseByItemId(existingItemId, quantity);
            });

            // inventoryJpaRepository.decreaseStock 가 실제로 동작했는지 확인하기 위해 검증
            verify(inventoryAdapter).decreaseStock(existingItemId, quantity);
        }

        @DisplayName("itemId를 갖는 entity를 찾으면, stock을 차감하고 inventory를 반환한다")
        @Test
        void test1000() {
            // given
            final Long quantity = 10L;

            // when
            final Inventory result = sut.decreaseByItemId(existingItemId, quantity);

            // then
            assertNotNull(result);
            assertEquals(existingItemId, result.getItemId());
            assertEquals(stock - quantity, result.getStock());
        }
    }

    @Nested
    class UpdateStock {
        final String existingItemId = "1";
        final Long stock = 10L;

        @BeforeEach
        void setUp() {
            inventoryAdapter.addInventory(existingItemId, stock);
        }

        @DisplayName("수정할 stock이 유효하지 않다면, Exception을 throw한다")
        @Test
        void test1() {
            // given
            final Long stock = -1L;

            // when & then
            assertThrows(InvalidStockException.class, () -> {
                sut.updateStock(existingItemId, stock);
            });
        }

        @DisplayName("itemId를 갖는 entity를 찾지 못하면, Exception을 throw한다")
        @Test
        void test2() {
            // given
            final String nonExistingItemId = "2";
            final Long stock = 10L;

            // when & then
            assertThrows(ItemNotFoundException.class, () -> {
                sut.updateStock(nonExistingItemId, stock);
            });
        }

        @DisplayName("itemId를 갖는 entity를 찾으면, stock을 수정하고 inventory를 반환한다")
        @Test
        void test1000() {
            // given
            final Long newStock = 20L;

            // when
            final Inventory result = sut.updateStock(existingItemId, newStock);

            // then
            assertNotNull(result);
            assertEquals(existingItemId, result.getItemId());
            assertEquals(newStock, result.getStock());
        }
    }
}
