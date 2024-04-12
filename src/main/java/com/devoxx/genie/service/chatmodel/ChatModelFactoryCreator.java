package com.devoxx.genie.service.chatmodel;

import com.devoxx.genie.config.ApplicationProperties;
import com.devoxx.genie.domain.enumeration.LanguageModelType;
import com.devoxx.genie.service.chatmodel.anthropic.AnthropicChatModelFactory;
import com.devoxx.genie.service.chatmodel.deepinfra.DeepInfraChatModelFactory;
import com.devoxx.genie.service.chatmodel.fireworks.FireworksChatModelFactory;
import com.devoxx.genie.service.chatmodel.gemini.GeminiChatModelFactory;
import com.devoxx.genie.service.chatmodel.gpt4all.GPT4AllChatModelFactory;
import com.devoxx.genie.service.chatmodel.groq.GroqChatModelFactory;
import com.devoxx.genie.service.chatmodel.lmstudio.LMStudioChatModelFactory;
import com.devoxx.genie.service.chatmodel.mistral.MistralChatModelFactory;
import com.devoxx.genie.service.chatmodel.ollama.OllamaChatModelFactory;
import com.devoxx.genie.service.chatmodel.openai.OpenAIChatModelFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class ChatModelFactoryCreator {

    private final ApplicationProperties applicationProperties;

    public ChatModelFactoryCreator() {
        this.applicationProperties = null;
    }

    @Autowired
    public ChatModelFactoryCreator(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    public ChatModelFactory getFactory(LanguageModelType languageModelType) {
        return switch (languageModelType) {
            case OLLAMA -> new OllamaChatModelFactory();
            case OPENAI -> new OpenAIChatModelFactory();
            case LMSTUDIO -> new LMStudioChatModelFactory();
            case CLAUDE -> new AnthropicChatModelFactory();
            case GEMINI -> new GeminiChatModelFactory(applicationProperties);
            case GPT4ALL -> new GPT4AllChatModelFactory();
            case MISTRAL -> new MistralChatModelFactory();
            case GROQ -> new GroqChatModelFactory();
            case DEEPINFRA -> new DeepInfraChatModelFactory();
            case FIREWORKS -> new FireworksChatModelFactory();
            case EMBED -> throw new UnsupportedOperationException("Embed chat not supported");
            case COHERE -> throw new UnsupportedOperationException("Cohere chat not supported");
            case SERPAPI -> throw new UnsupportedOperationException("SERPAPI not yet supported");
        };
    }
}
