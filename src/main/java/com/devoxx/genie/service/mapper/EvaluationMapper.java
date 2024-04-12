package com.devoxx.genie.service.mapper;

import com.devoxx.genie.domain.Evaluation;
import com.devoxx.genie.service.dto.EvaluationDTO;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.Arrays;
import java.util.List;

/**
 * Mapper for the entity Evaluation and its DTO EvaluationDTO.
 */
@AnnotateWith(GeneratedMapper.class)
@Mapper(componentModel = "spring", uses = {EvaluationMapper.class, LanguageModelMapper.class, UserMapper.class})
public interface EvaluationMapper extends EntityMapper<EvaluationDTO, Evaluation> {

    @Mapping(target = "userId", source = "user.id")
    @Mapping(target = "keywords", source = "keywords", qualifiedByName = "mapKeywordsToArray")
    EvaluationDTO toDto(Evaluation evaluation);

    @Mapping(target = "user", source = "userId")
    @Mapping(target = "keywords", source = "keywords", qualifiedByName = "mapKeywordsToString")
    Evaluation toEntity(EvaluationDTO evaluationDTO);

    @Named("mapKeywordsToString")
    default String mapKeywordsToString(List<String> keywords) {
        return String.join(",", keywords);
    }

    @Named("mapKeywordsToArray")
    default List<String> mapKeywords(String keywords) {
        return Arrays.asList(keywords.split(", "));
    }
}
