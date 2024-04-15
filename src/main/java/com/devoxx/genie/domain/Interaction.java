package com.devoxx.genie.domain;

import com.devoxx.genie.domain.enumeration.UserVote;
import org.hibernate.Length;

import java.time.ZonedDateTime;
import java.util.Objects;
import jakarta.persistence.*;

@Entity
@Table(name = "genie_interaction")
public class Interaction {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "created_on", columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = false, updatable = false)
    private ZonedDateTime createdOn;

    @Column(name = "question", nullable = false, length = Length.LONG32)
    private String question;

    @Column(name = "answer", nullable = false, length = Length.LONG32)
    private String answer;

    @Column(name = "duration_in_ms")
    private Long durationInMs;

    @Column(name = "input_tokens")
    private Integer inputTokens;

    @Column(name = "output_tokens")
    private Integer outputTokens;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "user_vote")
    @Enumerated(EnumType.STRING)
    private UserVote vote;

    @ManyToOne
    private EmbeddingModelReference embeddingModel;

    @ManyToOne
    private User user;

    @ManyToOne
    private LanguageModel languageModel;

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

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public Long getDurationInMs() {
        return durationInMs;
    }

    public void setDurationInMs(Long durationInMs) {
        this.durationInMs = durationInMs;
    }

    public Integer getInputTokens() {
        return inputTokens;
    }

    public void setInputTokens(Integer inputTokens) {
        this.inputTokens = inputTokens;
    }

    public Integer getOutputTokens() {
        return outputTokens;
    }

    public void setOutputTokens(Integer outputTokens) {
        this.outputTokens = outputTokens;
    }

    public Double getCost() {
        return cost;
    }

    public void setCost(Double cost) {
        this.cost = cost;
    }

    public UserVote getVote() {
        return vote;
    }

    public void setVote(UserVote vote) {
        this.vote = vote;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public LanguageModel getLanguageModel() {
        return languageModel;
    }

    public void setLanguageModel(LanguageModel languageModel) {
        this.languageModel = languageModel;
    }

    public EmbeddingModelReference getEmbeddingModel() {
        return embeddingModel;
    }

    public void setEmbeddingModel(EmbeddingModelReference embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Interaction that = (Interaction) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(createdOn, that.createdOn) &&
            Objects.equals(question, that.question) &&
            Objects.equals(answer, that.answer) &&
            Objects.equals(durationInMs, that.durationInMs) &&
            Objects.equals(inputTokens, that.inputTokens) &&
            Objects.equals(outputTokens, that.outputTokens) &&
            Objects.equals(cost, that.cost) &&
            vote == that.vote &&
            Objects.equals(user, that.user) &&
            Objects.equals(languageModel, that.languageModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdOn, question, answer, durationInMs, inputTokens, outputTokens, cost, vote, user, languageModel);
    }

    @Override
    public String toString() {
        return "InteractionHistory{" +
            "id=" + id +
            ", createdOn=" + createdOn +
            ", question='" + question + '\'' +
            ", answer='" + answer + '\'' +
            ", durationInMs=" + durationInMs +
            ", inputTokens=" + inputTokens +
            ", outputTokens=" + outputTokens +
            ", cost=" + cost +
            ", vote=" + vote +
            ", user=" + user +
            ", languageModel=" + languageModel +
            '}';
    }
}
