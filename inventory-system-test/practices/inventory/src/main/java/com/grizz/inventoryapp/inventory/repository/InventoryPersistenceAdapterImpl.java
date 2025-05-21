package com.grizz.inventoryapp.inventory.repository;

import com.grizz.inventoryapp.inventory.service.domain.Inventory;
import com.grizz.inventoryapp.inventory.service.persistence.InventoryPersistenceAdapter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryPersistenceAdapterImpl implements InventoryPersistenceAdapter {
    @Override
    public @Nullable Inventory decreaseStock(@NotNull String itemId, @NotNull Long quantity) {
        return null;
    }

    @Override
    public @Nullable Inventory findByItemId(@NotNull String itemId) {
        return null;
    }

    @Override
    public @NotNull Inventory save(Inventory inventory) {
        return null;
    }
}
