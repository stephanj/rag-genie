package com.devoxx.genie.service.gpt;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

public interface QuestionWithContextService {

    @SystemMessage("You are a helpful chat bot that answers questions based on the given context. Use HTML to wrap your answer and <UL> when using bullet points.")
    @UserMessage("{{context}} question: {{question}}")
    String question(@V("question") String question, @V("context") String context);

}
