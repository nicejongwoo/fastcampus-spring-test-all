package com.grizz.inventoryapp.controller.dto;

import org.jetbrains.annotations.NotNull;

public record DecreaseQuantityRequest(
        @NotNull Long quantity
) {
}
