package com.grizz.inventoryapp.inventory.service;

import com.grizz.inventoryapp.inventory.service.domain.Inventory;
import com.grizz.inventoryapp.inventory.service.event.InventoryDecreasedEvent;
import com.grizz.inventoryapp.inventory.service.event.InventoryEventPublisher;
import com.grizz.inventoryapp.inventory.service.exception.InsufficientStockException;
import com.grizz.inventoryapp.inventory.service.exception.InvalidDecreaseQuantityException;
import com.grizz.inventoryapp.inventory.service.exception.InvalidStockException;
import com.grizz.inventoryapp.inventory.service.exception.ItemNotFoundException;
import com.grizz.inventoryapp.inventory.service.persistence.InventoryPersistenceAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class InventoryService {

    private final InventoryPersistenceAdapter inventoryAdapter;
    private final InventoryEventPublisher inventoryEventPublisher;

    public InventoryService(InventoryPersistenceAdapter inventoryAdapter, InventoryEventPublisher inventoryEventPublisher) {
        this.inventoryAdapter = inventoryAdapter;
        this.inventoryEventPublisher = inventoryEventPublisher;
    }

    public @Nullable Inventory findByItemId(@NotNull String itemId) {
        return inventoryAdapter.findByItemId(itemId);
    }

    @Transactional
    public @NotNull Inventory decreaseByItemId(@NotNull String itemId, @NotNull Long quantity) {
        if (quantity < 0) { // quantity 가 음수라면
            throw new InvalidDecreaseQuantityException();
        }

        final Inventory inventory = inventoryAdapter.findByItemId(itemId);

        if (inventory == null) {
            throw new ItemNotFoundException();
        }

        if (inventory.getStock() < quantity) {
            throw new InsufficientStockException();
        }

        final Inventory updateCount = inventoryAdapter.decreaseStock(itemId, quantity);
        if (updateCount == null) {
            throw new ItemNotFoundException();
        }

        final Inventory updatedInventory = inventoryAdapter.findByItemId(itemId);

        if (updatedInventory == null) {
            throw new ItemNotFoundException();
        }

        InventoryDecreasedEvent even = new InventoryDecreasedEvent(itemId, quantity, updatedInventory.getStock());
        inventoryEventPublisher.publish(even);

        return updatedInventory;
    }

    public @NotNull Inventory updateStock(@NotNull String itemId, @NotNull Long stock) {
        if (stock < 0) {
            throw new InvalidStockException();
        }

        final Inventory inventory = inventoryAdapter.findByItemId(itemId);

        if (inventory == null) {
            throw new ItemNotFoundException();
        }

        inventory.setStock(stock);

        return inventoryAdapter.save(inventory);
    }

}
