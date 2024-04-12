package com.devoxx.genie.service.mapper;

import com.devoxx.genie.domain.EvaluationResult;
import com.devoxx.genie.service.dto.EvaluationResultDTO;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity EvaluationResult and its DTO EvaluationResultDTO.
 */
@AnnotateWith(GeneratedMapper.class)
@Mapper(componentModel = "spring",
    uses = {LanguageModelMapper.class, EvaluationMapper.class, UserMapper.class, EmbeddingModelReferenceMapper.class})
public interface EvaluationResultMapper extends EntityMapper<EvaluationResultDTO, EvaluationResult> {

    @Mapping(source = "user.id", target = "userId")
    EvaluationResultDTO toDto(EvaluationResult evaluationEntry);

    @Mapping(source = "userId", target = "user")
    EvaluationResult toEntity(EvaluationResultDTO evaluationEntryDTO);
}
