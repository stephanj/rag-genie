package com.devoxx.genie.domain;

import jakarta.persistence.*;
import org.hibernate.Length;

import java.util.Objects;

@Entity
@Table(name = "genie_evaluation")
public class Evaluation {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "question", nullable = false, length = Length.LONG32)
    private String question;

    @Column(name = "answer", nullable = false, length = Length.LONG32)
    private String answer;

    @Column(name = "keywords")
    private String keywords;

    @ManyToOne
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

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public String getKeywords() {
        return keywords;
    }

    public void setKeywords(String keywords) {
        this.keywords = keywords;
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
        Evaluation that = (Evaluation) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(question, that.question) &&
            Objects.equals(answer, that.answer) &&
            Objects.equals(keywords, that.keywords);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, question, answer, keywords);
    }

    @Override
    public String toString() {
        return "Evaluation{" +
            "id=" + id +
            ", question='" + question + '\'' +
            ", answer='" + answer + '\'' +
            ", keywords='" + keywords + '\'' +
            '}';
    }
}
