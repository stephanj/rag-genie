package com.devoxx.genie.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Setter
@Getter
public class EmbeddingResponseDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private float[][] vectors;
    private String color;
    private String source;
    private String contentId;
}
