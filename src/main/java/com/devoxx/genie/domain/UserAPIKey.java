package com.devoxx.genie.domain;

import com.devoxx.genie.domain.enumeration.LanguageModelType;
import jakarta.persistence.*;

import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "genie_user_api_key")
public class UserAPIKey extends AbstractAuditingEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "key_mask")
    private String keyMask;

    @Column(name = "language_type")
    @Enumerated(EnumType.STRING)
    private LanguageModelType languageType;

    @Column(name = "api_key")
    private String apiKey;

    @Column(name = "last_used")
    private ZonedDateTime lastUsed;

    @OneToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKeyMask() {
        return keyMask;
    }

    public void setKeyMask(String keyMask) {
        this.keyMask = keyMask;
    }

    public ZonedDateTime getLastUsed() {
        return lastUsed;
    }

    public void setLastUsed(ZonedDateTime lastUsed) {
        this.lastUsed = lastUsed;
    }

    public LanguageModelType getLanguageType() {
        return languageType;
    }

    public void setLanguageType(LanguageModelType languageType) {
        this.languageType = languageType;
    }

    public String getApiKey() {
        return apiKey;
    }

    public void setApiKey(String apiKey) {
        this.apiKey = apiKey;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        UserAPIKey that = (UserAPIKey) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(keyMask, that.keyMask) &&
            languageType == that.languageType &&
            Objects.equals(apiKey, that.apiKey) &&
            Objects.equals(lastUsed, that.lastUsed) &&
            Objects.equals(user, that.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, keyMask, languageType, apiKey, lastUsed, user);
    }

    @Override
    public String toString() {
        return "UserAPIKey{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", keyMask='" + keyMask + '\'' +
            ", languageType=" + languageType +
            ", apiKey='" + apiKey + '\'' +
            ", lastUsed=" + lastUsed +
            ", user=" + user +
            '}';
    }
}
