package com.devoxx.genie.domain;

import jakarta.persistence.*;
import org.hibernate.Length;

import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

@Entity
@Table(name = "genie_evaluation_result")
public class EvaluationResult {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    // CreatedOn
    @Column(name = "created_on", columnDefinition = "TIMESTAMP WITH TIME ZONE", insertable = false, updatable = false)
    private ZonedDateTime createdOn;

    @Column(name = "keyword_match")
    private Double keywordMatch;

    @Column(name = "similarity_score")
    private Double similarityScore;

    @Column(name = "answer", nullable = false, length = Length.LONG32)
    private String answer;

    @Column(name = "temperature", nullable = false)
    private float temperature;

    @Column(name = "min_score", nullable = false)
    private int minScore;

    @Column(name = "max_documents", nullable = false)
    private int maxDocuments;

    @Column(name = "duration_in_ms", nullable = false)
    private Long durationInMs;

    @Column(name = "input_tokens", nullable = false)
    private int inputTokens;

    @Column(name = "output_tokens", nullable = false)
    private int outputTokens;

    @Column(name = "cost", nullable = false)
    private double cost;

    @Column(name = "rerank")
    private boolean rerank;

    @ManyToOne
    private Evaluation evaluation;

    @ManyToOne
    private LanguageModel languageModel;

    @ManyToOne
    private EmbeddingModelReference embeddingModelReference;

    @ManyToOne
    private User user;

//    @ManyToMany
//    @JoinTable(name = "genie_evaluation_result_document",
//        joinColumns = @JoinColumn(name = "evaluation_result_id", referencedColumnName = "id"),
//        inverseJoinColumns = @JoinColumn(name = "document_id", referencedColumnName = "embedding_id"))
//    private Set<Document> usedDocuments = new HashSet<>();

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

    public Double getKeywordMatch() {
        return keywordMatch;
    }

    public void setKeywordMatch(Double keywordMatch) {
        this.keywordMatch = keywordMatch;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public LanguageModel getLanguageModel() {
        return languageModel;
    }

    public void setLanguageModel(LanguageModel languageModel) {
        this.languageModel = languageModel;
    }

    public Evaluation getEvaluation() {
        return evaluation;
    }

    public void setEvaluation(Evaluation evaluation) {
        this.evaluation = evaluation;
    }

    public Double getSimilarityScore() {
        return similarityScore;
    }

    public void setSimilarityScore(Double similarityScore) {
        this.similarityScore = similarityScore;
    }

    public Long getDurationInMs() {
        return durationInMs;
    }

    public void setDurationInMs(Long durationInMs) {
        this.durationInMs = durationInMs;
    }

    public float getTemperature() {
        return temperature;
    }

    public void setTemperature(float temperature) {
        this.temperature = temperature;
    }

    public int getMinScore() {
        return minScore;
    }

    public void setMinScore(int minScore) {
        this.minScore = minScore;
    }

    public int getMaxDocuments() {
        return maxDocuments;
    }

    public void setMaxDocuments(int maxDocuments) {
        this.maxDocuments = maxDocuments;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getInputTokens() {
        return inputTokens;
    }

    public void setInputTokens(int inputTokens) {
        this.inputTokens = inputTokens;
    }

    public int getOutputTokens() {
        return outputTokens;
    }

    public void setOutputTokens(int outputTokens) {
        this.outputTokens = outputTokens;
    }

    public double getCost() {
        return cost;
    }

    public void setCost(double cost) {
        this.cost = cost;
    }

    public boolean isRerank() {
        return rerank;
    }

    public void setRerank(boolean rerank) {
        this.rerank = rerank;
    }

    public EmbeddingModelReference getEmbeddingModelReference() {
        return embeddingModelReference;
    }

    public void setEmbeddingModelReference(EmbeddingModelReference embeddingModelReference) {
        this.embeddingModelReference = embeddingModelReference;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EvaluationResult that = (EvaluationResult) o;
        return Objects.equals(id, that.id) &&
            Objects.equals(createdOn, that.createdOn) &&
            Objects.equals(keywordMatch, that.keywordMatch) &&
            Objects.equals(similarityScore, that.similarityScore) &&
            Objects.equals(answer, that.answer) &&
            Objects.equals(durationInMs, that.durationInMs) &&
            Objects.equals(evaluation, that.evaluation) &&
            Objects.equals(languageModel, that.languageModel);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, createdOn, keywordMatch, similarityScore, answer, durationInMs, evaluation, languageModel);
    }

    @Override
    public String toString() {
        return "EvaluationResult{" +
            "id=" + id +
            ", createdOn=" + createdOn +
            ", keywordMatch=" + keywordMatch +
            ", similarityScore=" + similarityScore +
            ", answer='" + answer + '\'' +
            ", temperature=" + temperature +
            ", minScore=" + minScore +
            ", maxDocuments=" + maxDocuments +
            ", durationInMs=" + durationInMs +
            ", inputTokens=" + inputTokens +
            ", outputTokens=" + outputTokens +
            ", cost=" + cost +
            ", rerank=" + rerank +
            ", evaluation=" + evaluation +
            ", languageModel=" + languageModel +
            ", embeddingModelReference=" + embeddingModelReference +
            ", user=" + user +
            '}';
    }
}
