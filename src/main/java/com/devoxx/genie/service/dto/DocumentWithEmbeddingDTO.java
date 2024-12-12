package com.devoxx.genie.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
@Builder
public class DocumentWithEmbeddingDTO {
    private String text;
    private float[] embedding;
}
