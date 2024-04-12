package com.devoxx.genie.service.gpt;

import dev.langchain4j.model.chat.ChatLanguageModel;
import dev.langchain4j.service.AiServices;
import org.springframework.stereotype.Service;

/**
 * Service Implementation for ChatGPT using Langchain4j.
 *
 * @link <a href="https://github.com/langchain4j">Langchain4J</a>
 */
@Service
public class GPTService {

    public String getAnswerInHTMLFormat(ChatLanguageModel chatLanguageModel, String text) {
        return AiServices.create(FormattingUtil.class, chatLanguageModel).formatTextToHTML(text);
    }
}
