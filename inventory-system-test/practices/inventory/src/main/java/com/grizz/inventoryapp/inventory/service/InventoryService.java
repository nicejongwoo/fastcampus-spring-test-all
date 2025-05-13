package com.grizz.inventoryapp.inventory.service;

import com.grizz.inventoryapp.inventory.repository.InventoryJpaRepository;
import com.grizz.inventoryapp.inventory.repository.entity.InventoryEntity;
import com.grizz.inventoryapp.inventory.service.domain.Inventory;
import com.grizz.inventoryapp.inventory.service.exception.InsufficientStockException;
import com.grizz.inventoryapp.inventory.service.exception.InvalidDecreaseQuantityException;
import com.grizz.inventoryapp.inventory.service.exception.InvalidStockException;
import com.grizz.inventoryapp.inventory.service.exception.ItemNotFoundException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class InventoryService {

    private final InventoryJpaRepository inventoryJpaRepository;

    public InventoryService(InventoryJpaRepository inventoryJpaRepository) {
        this.inventoryJpaRepository = inventoryJpaRepository;
    }

    public @Nullable Inventory findByItemId(@NotNull String itemId) {
        return inventoryJpaRepository.findByItemId(itemId)
                .map(this::mapToDomain)
                .orElse(null);
    }

    public @NotNull Inventory decreaseByItemId(@NotNull String itemId, @NotNull Long quantity) {

        if (quantity < 0) { // quantity 가 음수라면
            throw new InvalidDecreaseQuantityException();
        }

        final InventoryEntity inventoryEntity = inventoryJpaRepository.findByItemId(itemId)
                .orElseThrow(ItemNotFoundException::new);

        if (inventoryEntity.getStock() < quantity) {
            throw new InsufficientStockException();
        }

        final Integer updateCount = inventoryJpaRepository.decreaseStock(itemId, quantity);
        if (updateCount == 0) {
            throw new ItemNotFoundException();
        }

        final InventoryEntity updateEntity = inventoryJpaRepository.findByItemId(itemId)
                .orElseThrow(ItemNotFoundException::new);

        return mapToDomain(updateEntity);
    }

    public @NotNull Inventory updateStock(@NotNull String itemId, @NotNull Long stock) {
        if (stock < 0) {
            throw new InvalidStockException();
        }

        final InventoryEntity entity = inventoryJpaRepository.findByItemId(itemId)
                .orElseThrow(ItemNotFoundException::new);

        entity.setStock(stock);

        return mapToDomain(inventoryJpaRepository.save(entity));
    }

    private Inventory mapToDomain(InventoryEntity entity) {
        return new Inventory(entity.getItemId(), entity.getStock());
    }

}
