package com.devoxx.genie.service.exception;

import java.io.Serial;

public class LanguageModelException extends RuntimeException {
    @Serial
    private static final long serialVersionUID = 1L;

    public LanguageModelException(String message, Throwable cause) {
        super(message, cause);
    }
}
