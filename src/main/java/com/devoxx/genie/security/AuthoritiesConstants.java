package com.devoxx.genie.security;

/**
 * Constants for Spring Security authorities.
 */
public final class AuthoritiesConstants {

    public static final String ADMIN = "ROLE_ADMIN";
    public static final String USER = "ROLE_USER";
    public static final String ANONYMOUS = "ROLE_ANONYMOUS";

    // This version of the project is an anonmyous version, so we will hard code the user id
    public static final Long HARD_CODED_USER_ID = 3L;

    private AuthoritiesConstants() {
    }
}
