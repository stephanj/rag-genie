package com.devoxx.genie.service.mapper;

import com.devoxx.genie.domain.EmbeddingModelReference;
import com.devoxx.genie.service.dto.EmbeddingModelReferenceDTO;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;

/**
 * Mapper for the entity EmbeddingModelReference and EmbeddingModelReferenceDTO.
 */
@AnnotateWith(GeneratedMapper.class)
@Mapper(componentModel = "spring")
public interface EmbeddingModelReferenceMapper extends EntityMapper<EmbeddingModelReferenceDTO, EmbeddingModelReference> {
    EmbeddingModelReferenceDTO toDto(EmbeddingModelReference embeddingModelReference);

    EmbeddingModelReference toEntity(EmbeddingModelReferenceDTO embeddingModelReferenceDTO);
}
