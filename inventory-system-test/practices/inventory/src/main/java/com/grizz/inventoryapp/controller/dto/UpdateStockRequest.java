package com.grizz.inventoryapp.controller.dto;

import org.jetbrains.annotations.NotNull;

public record UpdateStockRequest(
        @NotNull Long stock
) {
}
