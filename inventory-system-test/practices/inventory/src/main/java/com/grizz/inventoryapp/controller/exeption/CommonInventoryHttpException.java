package com.grizz.inventoryapp.controller.exeption;

import com.grizz.inventoryapp.controller.consts.ErrorCodes;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;

public class CommonInventoryHttpException extends RuntimeException{

    @NotNull
    private final ErrorCodes errorCodes;

    @NotNull
    private final HttpStatus httpStatus;

    public CommonInventoryHttpException(@NotNull ErrorCodes errorCodes,
                                        @NotNull HttpStatus httpStatus) {
        this.errorCodes = errorCodes;
        this.httpStatus = httpStatus;
    }

    public @NotNull ErrorCodes getErrorCodes() {
        return errorCodes;
    }

    public @NotNull HttpStatus getHttpStatus() {
        return httpStatus;
    }
}
