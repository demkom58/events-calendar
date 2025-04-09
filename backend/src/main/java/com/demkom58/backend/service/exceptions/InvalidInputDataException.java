package com.demkom58.backend.service.exceptions;

import jakarta.annotation.Nullable;

public class InvalidInputDataException extends RuntimeException {
    public InvalidInputDataException() {
    }

    public InvalidInputDataException(@Nullable String message) {
        super(message);
    }
}
