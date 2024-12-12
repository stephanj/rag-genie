package com.devoxx.genie.service.dto;

import lombok.Builder;
import lombok.Getter;

import java.io.Serializable;
import java.util.List;

@Builder
@Getter
public class RestEndpointDTO implements Serializable {
    private String path;
    private String method;
    private String operationId;
    private List<String> params;
}
