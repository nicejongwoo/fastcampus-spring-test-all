package com.grizz.inventoryapp.inventory.controller.dto;

import org.jetbrains.annotations.NotNull;

public record UpdateStockRequest(
        @NotNull Long stock
) {
}
