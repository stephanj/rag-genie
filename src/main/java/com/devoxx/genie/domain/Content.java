package com.devoxx.genie.domain;

import com.devoxx.genie.domain.enumeration.ContentType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import org.hibernate.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "genie_content")
public class Content implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "created_on", columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = false, updatable = false)
    private ZonedDateTime createdOn;

    @Column(name = "name", length = 255)
    private String name;

    @NotNull
    @Column(name = "source", nullable = false, length = Length.LONG32)
    private String source;

    @Column(name = "description", length = 255)
    private String description;

    @NotNull
    @Column(name = "value", nullable = false, length = Length.LONG32)
    private String value;

    @NotNull
    @Column(name = "content_type", nullable = false)
    @Enumerated(EnumType.STRING)
    private ContentType contentType;

    @Column(name = "estimated_token_count")
    private Integer tokenCount;

    @ManyToOne
    private User user;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public ZonedDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(ZonedDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public ContentType getContentType() {
        return contentType;
    }

    public void setContentType(ContentType contentType) {
        this.contentType = contentType;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public Integer getTokenCount() {
        return tokenCount;
    }

    public void setTokenCount(Integer tokenCount) {
        this.tokenCount = tokenCount;
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
        Content content = (Content) o;
        return Objects.equals(id, content.id) &&
            Objects.equals(createdOn, content.createdOn) &&
            Objects.equals(name, content.name) &&
            Objects.equals(source, content.source) &&
            Objects.equals(description, content.description) &&
            Objects.equals(value, content.value) &&
            contentType == content.contentType &&
            Objects.equals(tokenCount, content.tokenCount) &&
            Objects.equals(user, content.user);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdOn, name, source, description, value, contentType, tokenCount, user);
    }

    @Override
    public String toString() {
        return "Content{" +
            "id=" + id +
            ", createdOn=" + createdOn +
            ", name='" + name + '\'' +
            ", source='" + source + '\'' +
            ", description='" + description + '\'' +
            ", value='" + value + '\'' +
            ", contentType=" + contentType +
            ", tokenCount=" + tokenCount +
            ", user=" + user +
            '}';
    }
}
