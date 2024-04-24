package com.devoxx.genie.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.io.Serial;

public class EncryptingAesKeyException extends AbstractThrowableProblem {

    @Serial
    private static final long serialVersionUID = 1L;

    public EncryptingAesKeyException() {
        super(ErrorConstants.INVALID_AES_KEY, "Error while encrypting the AES key", Status.BAD_REQUEST);
    }
}
