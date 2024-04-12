package com.devoxx.genie.service.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Builder
@Getter
@Setter
public class SearchResultDTO {
    private String snippet;
    private String text;
    private String link;
}
