package com.devoxx.genie.web.rest.vm;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

/**
 * View Model object for storing a user's credentials.
 */
public class LoginVM {

    @NotNull
    @Size(min = 1, max = 50)
    private String username;

    @NotNull
    @Size(min = ManagedUserVM.PASSWORD_MIN_LENGTH, max = ManagedUserVM.PASSWORD_MAX_LENGTH)
    private String password;

    private Boolean rememberMe;

    private Boolean userRole;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public Boolean isRememberMe() {
        return Boolean.TRUE.equals(rememberMe);
    }

    public void setRememberMe(Boolean rememberMe) {
        this.rememberMe = rememberMe;
    }

    public Boolean isUserRole() {
        return Boolean.TRUE.equals(userRole);
    }

    public void setUserRole(Boolean userRole) {
        this.userRole = userRole;
    }

    @Override
    public String toString() {
        return """
            LoginVM{\
            username='\
            """ + username + '\'' +
            ", rememberMe=" + rememberMe +
            ", userRole=" + userRole +
            '}';
    }
}
