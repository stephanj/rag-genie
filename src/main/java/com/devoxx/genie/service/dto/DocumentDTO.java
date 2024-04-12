package com.devoxx.genie.service.dto;

import com.devoxx.genie.service.dto.enumeration.DocumentType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Builder
@Setter
@Getter
public class DocumentDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String id;
    private String text;
    @Builder.Default private Double score = 0D;
    private DocumentType docType;
    private String embedding;
    private String metadata;
    private String contentId;
    private String contentName;
    private Long embeddingModelRefId;
    private String embeddingModelName;
}
