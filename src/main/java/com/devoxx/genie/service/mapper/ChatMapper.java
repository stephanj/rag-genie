package com.devoxx.genie.service.mapper;

import com.devoxx.genie.domain.Chat;
import com.devoxx.genie.service.dto.ChatDTO;
import org.mapstruct.AnnotateWith;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * Mapper for the entity {@link Chat} and its DTO {@link ChatDTO}.
 */
@AnnotateWith(GeneratedMapper.class)
@Mapper(componentModel = "spring", uses = {UserMapper.class})
public interface ChatMapper extends EntityMapper<ChatDTO, Chat> {
    @Mapping(source = "user.id", target = "userId")
    @Mapping(source = "user.firstName", target = "userName")
    ChatDTO toDto(Chat chat);

    @Mapping(source = "userId", target = "user")
    Chat toEntity(ChatDTO chatDTO);

    default Chat fromId(Long id) {
        if (id == null) {
            return null;
        }
        Chat chat = new Chat();
        chat.setId(id);
        return chat;
    }
}
