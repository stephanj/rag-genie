package com.devoxx.genie.service.gpt;

import dev.langchain4j.service.MemoryId;
import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.UserName;

public interface ChatBotService {

    @SystemMessage("""
        You are a conference organizer of a tech conference. Today is {{current_date}}.
        """
    )
    String chat(@MemoryId int memoryId,
                @UserName String userName,
                @UserMessage String userMessage);

}
