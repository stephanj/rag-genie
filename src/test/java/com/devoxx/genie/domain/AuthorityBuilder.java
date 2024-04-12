package com.devoxx.genie.domain;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class AuthorityBuilder {

    @NotNull
    @Size(max = 50)
    private String name;

    public AuthorityBuilder name(String name) {
        this.name = name;
        return this;
    }

    public Authority build() {
        Authority authority = new Authority();
        authority.setName(name);
        return authority;
    }
}
