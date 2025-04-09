package com.demkom58.backend.service.exceptions;

import jakarta.annotation.Nullable;

public class ItemNotFoundException extends RuntimeException {
    public ItemNotFoundException() {
    }

    public ItemNotFoundException(@Nullable String message) {
        super(message);
    }
}
