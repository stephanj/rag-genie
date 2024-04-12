package com.devoxx.genie.service.entity;

import com.devoxx.genie.domain.Chat;
import com.devoxx.genie.repository.ChatRepository;
import com.devoxx.genie.service.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Transactional
@Service
public class ChatBotService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ChatBotService.class);

    private final ChatRepository chatRepository;
    private final UserService userService;

    public ChatBotService(ChatRepository chatRepository,
                          UserService userService) {
        this.chatRepository = chatRepository;
        this.userService = userService;
    }

    /**
     * Save a chat interaction.
     *
     * @param question the chat question
     * @param response the chat response
     */
    public void save(String question, String response) {
        LOGGER.debug("REST request to save a chat interaction: {}", question);

        userService.getAdminUser()
            .ifPresentOrElse(user -> {
                Chat chat = new Chat();
                chat.setUser(user);
                chat.setQuestion(question);
                chat.setResponse(response);
                chat.setGoodResponse(false);
                chatRepository.save(chat);
            }, () -> LOGGER.error("User not found"));
    }
}
