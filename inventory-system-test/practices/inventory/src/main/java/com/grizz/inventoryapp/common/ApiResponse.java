package com.grizz.inventoryapp.common;

import com.grizz.inventoryapp.inventory.controller.consts.ErrorCodes;
import org.jetbrains.annotations.Nullable;

public record ApiResponse<T>(
        @Nullable T data,
        @Nullable ApiErrorResponse error
) {
    public static <T> ApiResponse<T> just(T data) {
        return new ApiResponse<>(data, null);
    }

    public static <T> ApiResponse<T> fromErrorCodes(ErrorCodes errorCodes) {
        final ApiErrorResponse errorResponse = new ApiErrorResponse(errorCodes.message, errorCodes.code);
        return new ApiResponse<>(null, errorResponse);
    }
}
