package com.devoxx.genie.service.mapper.oauth2;

import java.util.Map;

public abstract class OAuth2UserInfoMapper {
    protected Map<String, Object> attributes;

    OAuth2UserInfoMapper(Map<String, Object> attributes) {
        this.attributes = attributes;
    }

    public Map<String, Object> getAttributes() {
        return attributes;
    }

    public abstract String getId();

    public abstract String getName();

    public abstract String getFirstName();

    public abstract String getLastName();

    public abstract String getEmail();

    public abstract String getImageUrl();

    public String toString() {
        return "id=" + getId() + "\n" +
            "name=" + getName() + "\n" +
            "firstName=" + getFirstName() + "\n" +
            "lastName=" + getLastName() + "\n" +
            "email=" + getEmail() + "\n" +
            "imageURL=" + getImageUrl();

    }
}
