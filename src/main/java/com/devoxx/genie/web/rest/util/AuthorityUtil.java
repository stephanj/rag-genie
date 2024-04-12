package com.devoxx.genie.web.rest.util;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.security.AuthoritiesConstants;
import com.devoxx.genie.security.SecurityUtils;
import com.devoxx.genie.service.user.UserService;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class AuthorityUtil {

    private final UserService userService;

    public AuthorityUtil(UserService userService) {
        this.userService = userService;
    }

    /**
     * Is authenticated user an admin?
     *
     * @return true when admin
     */
    public boolean isAdmin() {
        return SecurityUtils.isCurrentUserInRole(AuthoritiesConstants.ADMIN);
    }

    /**
     * Get user id from current authenticated user.
     *
     * @return user id
     */
    public Optional<User> getCurrentUser() {
        return userService.getAdminUser();
    }
}
