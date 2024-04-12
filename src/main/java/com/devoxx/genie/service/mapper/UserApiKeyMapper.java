package com.devoxx.genie.service.mapper;

import com.devoxx.genie.domain.UserAPIKey;
import com.devoxx.genie.service.dto.UserAPIKeyDTO;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity User API Key
 */
@AnnotateWith(GeneratedMapper.class)
@Mapper(componentModel = "spring", uses = {UserMapper.class, LanguageModelMapper.class})
public interface UserApiKeyMapper extends EntityMapper<UserAPIKeyDTO, UserAPIKey> {

    @Mapping(source = "user.id", target = "userId")
    UserAPIKeyDTO toDto(UserAPIKey userAPIKey);

    @Mapping(source = "userId", target = "user")
    UserAPIKey toEntity(UserAPIKeyDTO userAPIKeyDTO);
}
