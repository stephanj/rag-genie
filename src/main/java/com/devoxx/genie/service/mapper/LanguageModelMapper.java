package com.devoxx.genie.service.mapper;

import com.devoxx.genie.domain.LanguageModel;
import com.devoxx.genie.service.dto.LanguageModelDTO;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;

@AnnotateWith(GeneratedMapper.class)
@Mapper(componentModel = "spring")
public interface LanguageModelMapper extends EntityMapper<LanguageModelDTO, LanguageModel> {

    LanguageModelDTO toDto(LanguageModel languageModel);

    LanguageModel toEntity(LanguageModelDTO languageModelDTO);

}
