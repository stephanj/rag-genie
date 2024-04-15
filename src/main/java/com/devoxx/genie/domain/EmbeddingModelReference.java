package com.devoxx.genie.domain;

import com.devoxx.genie.domain.enumeration.LanguageModelType;
import org.hibernate.Length;

import java.util.Objects;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

@Entity
@Table(name = "genie_embedding_model")
public class EmbeddingModelReference {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "sequenceGenerator")
    @SequenceGenerator(name = "sequenceGenerator")
    private Long id;

    @NotNull
    @Size(max = 100)
    @Column(name = "slug", nullable = false)
    private String slug;

    @NotNull
    @Size(max = 255)
    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "dim_size")
    private Integer dimSize;

    @Column(name = "max_tokens")
    private Integer maxTokens;

    @Column(name = "description", nullable = false, length = Length.LONG32)
    private String description;

    @Column(name = "cost_usage_1m")
    private Double costUsage1m;

    @Column(name = "api_key_required", nullable = false)
    private boolean apiKeyRequired = false; // Default value is false

    @Size(max = 255)
    @Column(name = "website")
    private String website;

    @Column(name = "provider")
    @Enumerated(EnumType.STRING)
    private LanguageModelType provider;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getSlug() {
        return slug;
    }

    public void setSlug(String slug) {
        this.slug = slug;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getDimSize() {
        return dimSize;
    }

    public void setDimSize(Integer dimSize) {
        this.dimSize = dimSize;
    }

    public Integer getMaxTokens() {
        return maxTokens;
    }

    public void setMaxTokens(Integer maxTokens) {
        this.maxTokens = maxTokens;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Double getCostUsage1m() {
        return costUsage1m;
    }

    public void setCostUsage1m(Double costUsage1m) {
        this.costUsage1m = costUsage1m;
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

    public LanguageModelType getProvider() {
        return provider;
    }

    public void setProvider(LanguageModelType provider) {
        this.provider = provider;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EmbeddingModelReference that = (EmbeddingModelReference) o;
        return apiKeyRequired == that.apiKeyRequired &&
            Objects.equals(id, that.id) &&
            Objects.equals(name, that.name) &&
            Objects.equals(dimSize, that.dimSize) &&
            Objects.equals(description, that.description) &&
            Objects.equals(costUsage1m, that.costUsage1m) &&
            Objects.equals(website, that.website);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, name, dimSize, description, costUsage1m, apiKeyRequired, website);
    }

    @Override
    public String toString() {
        return "EmbeddingModel{" +
            "id=" + id +
            ", name='" + name + '\'' +
            ", dimSize=" + dimSize +
            ", description='" + description + '\'' +
            ", costUsage1m=" + costUsage1m +
            ", apiKeyRequired=" + apiKeyRequired +
            ", website='" + website + '\'' +
            '}';
    }
}
