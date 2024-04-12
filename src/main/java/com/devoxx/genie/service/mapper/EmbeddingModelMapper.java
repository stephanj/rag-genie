package com.devoxx.genie.service.mapper;

import com.devoxx.genie.domain.EmbeddingModelReference;
import com.devoxx.genie.service.dto.EmbeddingModelReferenceDTO;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;

@AnnotateWith(GeneratedMapper.class)
@Mapper(componentModel = "spring")
public interface EmbeddingModelMapper extends EntityMapper<EmbeddingModelReferenceDTO, EmbeddingModelReference> {

    EmbeddingModelReferenceDTO toDto(EmbeddingModelReference embeddingModelReference);

    EmbeddingModelReference toEntity(EmbeddingModelReferenceDTO embeddingModelDTO);
}
