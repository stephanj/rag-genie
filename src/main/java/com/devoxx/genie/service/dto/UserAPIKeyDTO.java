package com.devoxx.genie.service.dto;

import com.devoxx.genie.domain.enumeration.LanguageModelType;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;
import java.time.ZonedDateTime;

@Getter
@Setter
public class UserAPIKeyDTO {
    private Long id;
    private String name;
    private String keyMask;
    private String createdBy;
    private Instant createdDate;
    private String lastModifiedBy;
    private Instant lastModifiedDate;
    private LanguageModelType languageType;
    private String apiKey;
    private ZonedDateTime lastUsed;
    public Long userId;
}
