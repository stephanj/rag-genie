package com.devoxx.genie.service.chatmodel;

import com.devoxx.genie.service.dto.ChatModelDTO;
import dev.langchain4j.model.chat.ChatLanguageModel;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public abstract class AbstractChatModelFactory implements ChatModelFactory {

    private String apiKey;
    private String baseUrl;

    /**
     * Create a chat model with the given parameters.
     * @param chatModelDTO the chat model DTO
     * @return the chat model
     */
    public abstract ChatLanguageModel createChatModel(ChatModelDTO chatModelDTO);
}
