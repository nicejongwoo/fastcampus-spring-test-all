package com.grizz.inventoryapp.common;

import org.jetbrains.annotations.NotNull;

public record ApiErrorResponse(
        @NotNull String localMessage,
        @NotNull Long code
) {
}
