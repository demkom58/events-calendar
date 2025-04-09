package com.demkom58.backend.controller;

import com.demkom58.backend.service.exceptions.ItemNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.ErrorResponse;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ControllerGlobalExceptionHandler {
    private static final Logger log = LoggerFactory.getLogger(ControllerGlobalExceptionHandler.class);

    @ExceptionHandler
    public ErrorResponse itemNotFound404(ItemNotFoundException e) {
        return ErrorResponse.create(e, HttpStatusCode.valueOf(404), e.getMessage());
    }

    @ExceptionHandler
    public ErrorResponse argumentNotValid400(MethodArgumentNotValidException e) {
        return ErrorResponse.builder(e, e.getBody()).build();
    }

    @ExceptionHandler
    public ErrorResponse generic500(Exception e) {
        log.error("Internal server error", e);
        return ErrorResponse.create(e, HttpStatusCode.valueOf(500), "Internal Server Error");
    }
}
