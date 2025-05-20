package com.grizz.inventoryapp.inventory.service.persistence;

import com.grizz.inventoryapp.inventory.service.domain.Inventory;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicLong;

public class InventoryPersistenceAdapterStub implements InventoryPersistenceAdapter{

    private final List<Inventory> inventoryList = new ArrayList<>();
    private final AtomicLong idGenerator = new AtomicLong(1);

    public void addInventory(@NotNull String itemId, @NotNull Long stock) {
        final Long id = idGenerator.getAndIncrement();
        inventoryList.add(new Inventory(id, itemId, stock));
    }

    @Override
    public @Nullable Inventory findByItemId(@NotNull String itemId) {
        return internalFindByItemId(itemId).orElse(null);
    }

    @Override
    public @Nullable Inventory decreaseStock(@NotNull String itemId, @NotNull Long quantity) {
        return null;
    }

    private @NotNull Optional<Inventory> internalFindByItemId(@NotNull String itemId) {
        return inventoryList.stream()
                .filter(entity -> entity.getItemId().equals(itemId))
                .findFirst();
    }

}
