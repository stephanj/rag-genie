package com.devoxx.genie.security;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.repository.UserRepository;
import org.apache.commons.validator.routines.EmailValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Locale;

/**
 * Authenticate a user from the database.
 */
@Component("userDetailsService")
public class DomainUserDetailsService implements UserDetailsService {

    private static final Logger LOGGER = LoggerFactory.getLogger(DomainUserDetailsService.class);

    private final UserRepository userRepository;

    private final EmailValidator emailValidator = EmailValidator.getInstance();

    public DomainUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Load a user from the database.
     *
     * @param login the username identifying the user whose data is required.
     * @return a fully populated user record (never {@code null})
     */
    @Override
    @Transactional
    public UserDetails loadUserByUsername(final String login) {
        LOGGER.debug(">>>>>> Authenticating {}", login);

        String lowercaseLogin = login.toLowerCase(Locale.ENGLISH);

        if (emailValidator.isValid(lowercaseLogin)) {
            return userRepository.findOneWithAuthoritiesByEmail(lowercaseLogin)
                .map(user -> createSpringSecurityUser(lowercaseLogin, user))
                .orElseThrow(() -> new UsernameNotFoundException("User with email " + lowercaseLogin + " was not found in the database"));
        }

        return userRepository.findOneWithAuthoritiesByLogin(lowercaseLogin)
            .map(user -> createSpringSecurityUser(lowercaseLogin, user))
            .orElseThrow(() -> new UsernameNotFoundException("User " + lowercaseLogin + " was not found in the database"));
    }

    /**
     * Creates a Spring Security User from a User entity.
     *
     * @param lowercaseLogin the login of the user
     * @param user           the entity
     * @return the Spring Security User
     */
    private org.springframework.security.core.userdetails.User createSpringSecurityUser(String lowercaseLogin, User user) {
        if (!user.getActivated()) {
            throw new UserNotActivatedException("User " + lowercaseLogin + " was not activated");
        }

        List<SimpleGrantedAuthority> grantedAuthorities = user.getAuthorities().stream()
            .map(authority -> new SimpleGrantedAuthority(authority.getName()))
            .toList();

        return new org.springframework.security.core.userdetails.User(user.getLogin(),
            user.getPassword(),
            grantedAuthorities);
    }
}
