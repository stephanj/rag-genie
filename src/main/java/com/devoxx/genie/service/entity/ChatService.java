package com.devoxx.genie.service.entity;

import com.devoxx.genie.domain.Chat;
import com.devoxx.genie.repository.ChatRepository;
import com.devoxx.genie.service.dto.ChatDTO;
import com.devoxx.genie.service.mapper.ChatMapper;
import com.devoxx.genie.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;

@Transactional
@Service
public class ChatService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatService.class);

    private final ChatRepository chatRepository;
    private final ChatMapper chatMapper;
    private final UserService userService;

    public ChatService(ChatRepository chatRepository,
                       ChatMapper chatMapper,
                       UserService userService) {
        this.chatRepository = chatRepository;
        this.chatMapper = chatMapper;
        this.userService = userService;
    }

    public void save(String question, String response) {
        LOGGER.debug("REST request to save a chat interaction: {}", question);

        userService.getAdminUser()
            .ifPresentOrElse(user -> {
                Chat chat = new Chat();
                chat.setUser(user);
                chat.setCreatedOn(ZonedDateTime.now());
                chat.setQuestion(question);
                chat.setResponse(response);
                chat.setGoodResponse(false);
                chatRepository.save(chat);
            }, () -> LOGGER.error("User not found"));
    }

    /**
     * Save a chat
     *
     * @param chatDTO the chat DTO
     */
    public ChatDTO save(ChatDTO chatDTO) {
        LOGGER.debug("Save a chat: {}", chatDTO);
        Chat save = chatRepository.save(chatMapper.toEntity(chatDTO));
        return chatMapper.toDto(save);
    }

    /**
     * Find a chat by id
     *
     * @param chatId the chat id
     * @return the chat DTO
     */
    public ChatDTO findOne(Long chatId) {
        LOGGER.debug("REST request to get a chat");
        return chatRepository.findById(chatId)
            .map(chatMapper::toDto)
            .orElseThrow();
    }

    /**
     * Find all chats.
     *
     * @param pageable the pagination information
     * @return the list of chats
     */
    public Page<ChatDTO> findAll(Pageable pageable) {
        LOGGER.debug("REST request to get all chats");
        return chatRepository.findAll(pageable)
            .map(chatMapper::toDto);
    }

    /**
     * Delete a chat by id
     *
     * @param id the chat id
     */
    public void delete(Long id) {
        LOGGER.debug("REST request to delete a chat");
        chatRepository.deleteById(id);
    }
}
