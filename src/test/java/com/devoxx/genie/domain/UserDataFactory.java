package com.devoxx.genie.domain;

import net.datafaker.Faker;

import java.util.List;
import java.util.Random;

public class UserDataFactory {

    private final static Faker faker = new Faker(new Random());

    private UserDataFactory() {
    }

    public static User create() {
        return new UserBuilder()
            .firstName(faker.name().firstName())
            .lastName(faker.name().lastName())
            .fullName(faker.name().fullName())
            .login(faker.internet().username())
            .email(faker.internet().emailAddress())
            .password(faker.internet().password(60,60))
            .activated(true)
            .imageUrl(faker.internet().url())
            .build();
    }

    /**
     * Create a list of users.
     *
     * @param max the maximum number (excluded) of users to create
     * @return a list of users
     */
    public static List<User> createMultiple(int max) {
        return faker.collection(UserDataFactory::create).len(1, max).generate();
    }
}
