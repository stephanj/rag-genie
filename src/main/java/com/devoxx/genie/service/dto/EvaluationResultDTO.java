package com.devoxx.genie.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
public class EvaluationResultDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private ZonedDateTime createdOn;
    private Double similarityScore;
    private String answer;
    private Double keywordMatch;
    private LanguageModelDTO languageModel;
    private EmbeddingModelReferenceDTO embeddingModelReference;
    private EvaluationDTO evaluation;
    private Long durationInMs;
    private Double temperature;
    public Long userId;
    private Integer inputTokens;
    private Integer outputTokens;
    private Boolean rerank;
    private Double cost;
    private Set<DocumentDTO> usedDocuments = new HashSet<>();
}
