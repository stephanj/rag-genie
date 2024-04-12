package com.devoxx.genie.service.dto;

import java.time.ZonedDateTime;

/**
 * A DTO for the {@link com.devoxx.genie.domain.Chat} entity.
 */
public record ChatDTO(
    Long id,
    Long userId,
    String userName,
    ZonedDateTime createdOn,
    String question,
    String response,
    Boolean goodResponse
) {
}
