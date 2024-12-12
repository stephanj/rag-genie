package com.devoxx.genie.service.dto;

import com.devoxx.genie.service.dto.enumeration.ContentType;
import com.devoxx.genie.service.retriever.swagger.Field;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.io.Serial;
import java.io.Serializable;
import java.time.ZonedDateTime;
import java.util.List;
import jakarta.validation.constraints.NotNull;

@Getter
@Setter
@ToString
@EqualsAndHashCode
public class ContentDTO implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long userId;
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSZ")
    private ZonedDateTime createdOn;
    private ContentType contentType;
    private String name;
    @NotNull
    private String source;
    private String description;
    private String value;
    private String fullName;
    private String metaData;
    private List<Field> fields;
    private String restTemplate;
    private Long tokenCount;
}
