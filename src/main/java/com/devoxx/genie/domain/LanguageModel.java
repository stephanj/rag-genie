package com.devoxx.genie.domain;

import com.devoxx.genie.domain.enumeration.LanguageModelType;
import org.hibernate.Length;

import java.util.Objects;
import jakarta.persistence.*;

@Entity
@Table(name = "genie_language_model")
public class LanguageModel {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description", nullable = false, length = Length.LONG32)
    private String description;

    @Column(name = "base_url")
    private String baseUrl;

    @Column(name = "version")
    private String version;

    @Column(name = "model_type")
    @Enumerated(EnumType.STRING)
    private LanguageModelType modelType;

    @Column(name = "cost_input_1m")
    private Double costInput1M;

    @Column(name = "cost_output_1m")
    private Double costOutput1M;

    @Column(name = "tokens")
    private Boolean tokens;

    @Column(name = "context_window")
    private Integer contextWindow;

    @Column(name = "params_size")
    private Double paramsSize;

    @Column(name = "api_key_required")
    private boolean apiKeyRequired;

    @Column(name = "website")
    private String website;

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

    public String getBaseUrl() {
        return baseUrl;
    }

    public void setBaseUrl(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public LanguageModelType getModelType() {
        return modelType;
    }

    public void setModelType(LanguageModelType modelType) {
        this.modelType = modelType;
    }

    public Integer getContextWindow() {
        return contextWindow;
    }

    public void setContextWindow(Integer contextWindow) {
        this.contextWindow = contextWindow;
    }

    public Double getCostInput1M() {
        return costInput1M;
    }

    public void setCostInput1M(Double costInput1K) {
        this.costInput1M = costInput1K;
    }

    public Double getCostOutput1M() {
        return costOutput1M;
    }

    public void setCostOutput1M(Double costOutput1K) {
        this.costOutput1M = costOutput1K;
    }

    public Boolean getTokens() {
        return tokens;
    }

    public void setTokens(Boolean tokens) {
        this.tokens = tokens;
    }

    public Double getParamsSize() {
        return paramsSize;
    }

    public void setParamsSize(Double paramsSize) {
        this.paramsSize = paramsSize;
    }

    public boolean isApiKeyRequired() {
        return apiKeyRequired;
    }

    public void setApiKeyRequired(boolean apiKeyRequired) {
        this.apiKeyRequired = apiKeyRequired;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LanguageModel that = (LanguageModel) o;
        return apiKeyRequired == that.apiKeyRequired &&
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(description, that.description) &&
            Objects.equals(baseUrl, that.baseUrl) &&
            Objects.equals(version, that.version) &&
            modelType == that.modelType &&
            Objects.equals(costInput1M, that.costInput1M) &&
            Objects.equals(costOutput1M, that.costOutput1M) &&
            Objects.equals(tokens, that.tokens) &&
            Objects.equals(contextWindow, that.contextWindow) &&
            Objects.equals(paramsSize, that.paramsSize) &&
            Objects.equals(website, that.website);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id,
            name,
            description,
            baseUrl,
            version,
            modelType,
            costInput1M,
            costOutput1M,
            tokens,
            contextWindow,
            paramsSize,
            apiKeyRequired,
            website);
    }

    @Override
    public String toString() {
        return "LanguageModel{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", description='" + description + '\'' +
            ", baseUrl='" + baseUrl + '\'' +
            ", version='" + version + '\'' +
            ", modelType=" + modelType +
            ", costInput1M=" + costInput1M +
            ", costOutput1M=" + costOutput1M +
            ", tokens=" + tokens +
            ", contextWindow=" + contextWindow +
            ", paramsSize=" + paramsSize +
            ", apiKeyRequired=" + apiKeyRequired +
            ", website='" + website + '\'' +
            '}';
    }
}
