package com.devoxx.genie.service.exception;

import java.io.Serial;

public class SemanticSearchException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public SemanticSearchException(String message, Throwable cause) {
        super(message, cause);
    }
}
