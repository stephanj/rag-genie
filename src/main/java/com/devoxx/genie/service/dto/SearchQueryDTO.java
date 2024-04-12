package com.devoxx.genie.service.dto;

import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.io.Serializable;

@Getter
@Setter
public class SearchQueryDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private String query;
    private Integer totalResults;
}
