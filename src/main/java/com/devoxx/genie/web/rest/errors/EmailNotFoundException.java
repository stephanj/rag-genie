package com.devoxx.genie.web.rest.errors;

import org.zalando.problem.AbstractThrowableProblem;
import org.zalando.problem.Status;

import java.io.Serial;

public class EmailNotFoundException extends AbstractThrowableProblem {

    @Serial
    private static final long serialVersionUID = 1L;

    public EmailNotFoundException() {
        super(ErrorConstants.EMAIL_NOT_FOUND_TYPE, "Email address not registered", Status.BAD_REQUEST);
    }
}
