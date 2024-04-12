package com.devoxx.genie.service.dto;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class EvalParamDTO {
    @Builder.Default
    private double temperature = 0.7;

    @Builder.Default
    private int maxResults = 10;

    @Builder.Default
    private int maxOutputTokens = 2000;

    @Builder.Default
    private double minScore = 0.6;

    @Builder.Default
    private boolean rerank = false;
}
