package com.devoxx.genie.service.mapper;

import com.devoxx.genie.domain.Content;
import com.devoxx.genie.service.dto.ContentDTO;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity Content and its DTO ContentDTO.
 */
@AnnotateWith(GeneratedMapper.class)
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ContentMapper extends EntityMapper<ContentDTO, Content> {

    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.fullName", target = "fullName")
    ContentDTO toDto(Content content);

    @Mapping(source = "userId", target = "user")
    Content toEntity(ContentDTO contentDTO);
}
