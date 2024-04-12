package com.devoxx.genie.service.dto;

import dev.langchain4j.model.output.FinishReason;
import dev.langchain4j.model.output.Response;
import dev.langchain4j.model.output.TokenUsage;

public class GenieResponse<T> extends Response<T> {

    private String htmlAnswer;

    public GenieResponse(T data, TokenUsage tokenUsage, FinishReason finishReason) {
        super(data, tokenUsage, finishReason);
    }

    public GenieResponse(T data, TokenUsage tokenUsage, FinishReason finishReason, String htmlAnswer) {
        super(data, tokenUsage, finishReason);
        this.htmlAnswer = htmlAnswer;
    }

    public String getHtmlAnswer() {
        return htmlAnswer;
    }
}
