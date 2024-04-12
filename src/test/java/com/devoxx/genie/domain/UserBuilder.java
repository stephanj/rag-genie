package com.devoxx.genie.domain;

import com.devoxx.genie.config.Constants;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.hibernate.annotations.Formula;

import jakarta.persistence.Column;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public class UserBuilder {

    @NotNull
    @Pattern(regexp = Constants.LOGIN_REGEX)
    @Size(min = 4, max = 50)
    private String login;

    @JsonIgnore
    @Size(min = 60, max = 60)
    private String password;

    @Size(max = 50)
    private String firstName;

    @Size(max = 50)
    private String lastName;

    // Must be public for thyme-leaf proposals digest template
    @Formula(value = " concat(first_name, ' ', last_name) ")
    public String fullName;

    @Email
    @Size(min = 5, max = 254)
    private String email;

    @NotNull
    private boolean activated = true;

    @Column(name = "image_url")
    private String imageUrl;

    public UserBuilder login(String login) {
        this.login = login;
        return this;
    }

    public UserBuilder password(String password) {
        this.password = password;
        return this;
    }

    public UserBuilder firstName(String firstName) {
        this.firstName = firstName;
        return this;
    }

    public UserBuilder lastName(String lastName) {
        this.lastName = lastName;
        return this;
    }

    public UserBuilder fullName(String fullName) {
        this.fullName = fullName;
        return this;
    }

    public UserBuilder email(String email) {
        this.email = email;
        return this;
    }

    public UserBuilder activated(boolean activated) {
        this.activated = activated;
        return this;
    }

    public UserBuilder imageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
        return this;
    }

    public User build() {
        User user = new User();
        user.setLogin(login);
        user.setPassword(password);
        user.setFirstName(firstName);
        user.setLastName(lastName);
        user.setFullName(fullName);
        user.setEmail(email);
        user.setActivated(activated);
        user.setImageUrl(imageUrl);
        return user;
    }
}
