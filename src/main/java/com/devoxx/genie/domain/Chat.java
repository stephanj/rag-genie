package com.devoxx.genie.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import org.hibernate.Length;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.Objects;

@Entity
@Table(name = "genie_chat")
public class Chat implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "created_on")
    ZonedDateTime createdOn;

    @Column(name = "question", length = Length.LONG32)
    String question;

    @Column(name = "response", length = Length.LONG32)
    String response;

    @Column(name = "good_response")
    Boolean goodResponse;

    @ManyToOne
    @JsonIgnoreProperties("proposals")
    private User user;

    public Chat() {
    }

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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getResponse() {
        return response;
    }

    public void setResponse(String response) {
        this.response = response;
    }

    public Boolean getGoodResponse() {
        return goodResponse;
    }

    public void setGoodResponse(Boolean goodResponse) {
        this.goodResponse = goodResponse;
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
        Chat chat = (Chat) o;
        return Objects.equals(id, chat.id) &&
            Objects.equals(user, chat.user) &&
            Objects.equals(question, chat.question) &&
            Objects.equals(response, chat.response);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, user, question, response);
    }

    @Override
    public String toString() {
        return "Chat{" +
            "id=" + id +
            ", user=" + user +
            ", question='" + question + '\'' +
            ", response='" + response + '\'' +
            '}';
    }
}
