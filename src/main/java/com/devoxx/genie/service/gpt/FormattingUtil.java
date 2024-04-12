package com.devoxx.genie.service.gpt;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

/**
 * The FormattingUtil GPT service.
 */
public interface FormattingUtil {
    @SystemMessage("You are a master in formatting text to plain HTML.")
    @UserMessage("""
            Format the following text in plain HTML for insertion in an existing HTML page.
            -----
            text: {{text}}
        """)
    String formatTextToHTML(@V("text") String text);
}
