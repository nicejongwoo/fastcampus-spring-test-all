package com.grizz.inventoryapp.inventory.repository.redis;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface InventoryRedisRepository {

    @NotNull default String key(@NotNull String itemId) {
        return String.format("inventory:%s", itemId);
    }

    @Nullable Long getStock(@NotNull String itemId);

    @NotNull Long decreaseStock(@NotNull String itemId, @NotNull Long quantity);

    @NotNull Boolean deleteStock(@NotNull String itemId);
}
