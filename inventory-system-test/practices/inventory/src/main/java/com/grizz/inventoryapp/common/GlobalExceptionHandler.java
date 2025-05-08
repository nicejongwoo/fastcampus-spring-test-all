package com.grizz.inventoryapp.common;

import com.grizz.inventoryapp.controller.exeption.CommonInventoryHttpException;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.nio.charset.StandardCharsets;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(value = {CommonInventoryHttpException.class})
    public ResponseEntity<ApiResponse<Void>> handleCommonInventoryHttpException(
            CommonInventoryHttpException exception
    ) {
        final ApiResponse<Void> body = ApiResponse.fromErrorCodes(exception.getErrorCodes());
        final var contentType = new MediaType(MediaType.APPLICATION_JSON, StandardCharsets.UTF_8);
        final var status = exception.getHttpStatus();

        return ResponseEntity.status(status)
                .contentType(contentType)
                .body(body);
    }

}
