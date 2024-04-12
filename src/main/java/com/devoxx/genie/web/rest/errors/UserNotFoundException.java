package com.devoxx.genie.web.rest.errors;

import java.io.Serial;

public class UserNotFoundException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public UserNotFoundException(String msg) {
        super(msg);
    }
}
