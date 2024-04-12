package com.devoxx.genie.service.dto;

import com.devoxx.genie.domain.enumeration.UserVote;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
public class InteractionDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private ZonedDateTime createdOn;
    private String question;
    private String answer;
    private Long durationInMs;
    private Integer inputTokens;
    private Integer outputTokens;
    private Double cost;
    private UserVote vote;
    private Long userId;
    private LanguageModelDTO languageModel;
    private EmbeddingModelReferenceDTO embeddingModel;
    private List<DocumentDTO> usedDocuments;
}
