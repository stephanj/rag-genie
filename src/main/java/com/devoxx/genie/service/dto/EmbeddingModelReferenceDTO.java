package com.devoxx.genie.service.dto;

import com.devoxx.genie.domain.enumeration.LanguageModelType;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
@EqualsAndHashCode
public class EmbeddingModelReferenceDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private String name;
    private String slug;
    private Integer dimSize;
    private Integer maxTokens;
    private String description;
    private Double costUsage1m;
    private LanguageModelType provider;
    private boolean apiKeyRequired = false; // Default value is false
    private String website;
}
