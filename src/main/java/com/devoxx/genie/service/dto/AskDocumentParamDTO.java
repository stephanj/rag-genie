package com.devoxx.genie.service.dto;

import java.util.List;

public record AskDocumentParamDTO(
    String question,
    String prompt,
    Long modelId,
    List<DocumentDTO> documents,
    Double temperature,
    Double topP,
    int maxTokens,
    int maxRetries,
    int timeout
) {
}
