package com.devoxx.genie.domain;

import net.datafaker.Faker;

import java.util.Random;

public class AuthorityDataFactory {

    private static final Faker faker = new Faker(new Random());

    private AuthorityDataFactory() {
    }

    public static Authority create(String name) {
        return new AuthorityBuilder()
            .name(name)
            .build();
    }
}
