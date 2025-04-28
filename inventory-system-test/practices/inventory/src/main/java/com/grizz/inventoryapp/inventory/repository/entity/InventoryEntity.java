package com.grizz.inventoryapp.inventory.repository.entity;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class InventoryEntity {
    private @Nullable Long id;
    private @NotNull String itemId;
    private @NotNull Long stock;

    public InventoryEntity(@Nullable Long id,
                           @NotNull String itemId,
                           @NotNull Long stock) {
        this.id = id;
        this.itemId = itemId;
        this.stock = stock;
    }

    public @NotNull String getItemId() {
        return itemId;
    }

    public @NotNull Long geStock() {
        return stock;
    }
}
