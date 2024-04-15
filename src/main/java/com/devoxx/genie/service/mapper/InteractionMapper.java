package com.devoxx.genie.service.mapper;

import com.devoxx.genie.domain.Interaction;
import com.devoxx.genie.service.dto.InteractionDTO;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity Interaction and InteractionDTO.
 */
@AnnotateWith(GeneratedMapper.class)
@Mapper(
    componentModel = "spring",
    uses = {UserMapper.class, LanguageModelMapper.class, EmbeddingModelMapper.class})
public interface InteractionMapper extends EntityMapper<InteractionDTO, Interaction> {

    @Mapping(source = "embeddingModel", target = "embeddingModel")
    @Mapping(source = "languageModel", target = "languageModel")
    @Mapping(source = "user.id", target = "userId")
    InteractionDTO toDto(Interaction interaction);

    @Mapping(source = "languageModel", target = "languageModel")
    @Mapping(source = "embeddingModel", target = "embeddingModel")
    @Mapping(source = "userId", target = "user")
    Interaction toEntity(InteractionDTO interactionDTO);
}
