package com.practice.test.board.common.api;

import com.practice.test.board.common.api.dto.ErrorMessage;
import com.practice.test.board.common.exception.AccessDeniedException;
import com.practice.test.board.common.exception.ResourceNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;

@RestControllerAdvice
public class GlobalControllerAdvice {

    @ExceptionHandler(ResourceNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorMessage handleNoSuchElementException(ResourceNotFoundException e) {
        return new ErrorMessage(HttpStatus.NOT_FOUND.value(), e.getMessage(), LocalDateTime.now());
    }

    @ExceptionHandler(AccessDeniedException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorMessage handleAccessDeniedException(AccessDeniedException ex) {
        return new ErrorMessage(HttpStatus.FORBIDDEN.value(), ex.getMessage(), LocalDateTime.now());
    }

}
