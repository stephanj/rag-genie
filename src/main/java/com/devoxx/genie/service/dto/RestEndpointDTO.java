package com.devoxx.genie.service.dto;

import lombok.Builder;

import java.util.List;

@Builder
public class RestEndpointDTO {
    private String path;
    private String method;
    private String operationId;
    private List<String> params;
}
