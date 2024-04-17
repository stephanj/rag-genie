package com.devoxx.genie.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Builder
public class ChatModelDTO {
    @Setter
    private Long userId;
    @Setter
    private String question;
    @Builder.Default private Boolean allDocs = false;
    @Builder.Default private Double temperature = 0.7;
    @Builder.Default private Double topP = 0.7;
    @Builder.Default private int maxTokens = 2_000;
    @Builder.Default private int maxResults = 10;
    @Builder.Default private int maxRetries = 5;
    @Builder.Default private double minScore = 0.6;
    @Builder.Default private int timeout = 60;
    @Builder.Default private boolean rerank = false;
    @Builder.Default private boolean searchWeb = false;
    @Builder.Default private boolean formatResponse = false;
    @Setter
    private String prompt;
    private Long embeddingModelRefId;
    private String embeddingModelRefName;
    @Setter
    private LanguageModelDTO languageModelDTO;
}
