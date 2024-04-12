package com.devoxx.genie.web.rest.errors;


import java.io.Serial;
import java.io.Serializable;
import java.net.URISyntaxException;

public class ProposalCreationException extends RuntimeException implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    public ProposalCreationException(String message, URISyntaxException e) {
        super(message, e);
    }
}
