package com.devoxx.genie.service;

import com.knuddels.jtokkit.Encodings;
import com.knuddels.jtokkit.api.Encoding;
import com.knuddels.jtokkit.api.EncodingRegistry;
import com.knuddels.jtokkit.api.ModelType;

public class TokenService {

    private TokenService() {
    }

    private static final EncodingRegistry encodingRegistry = Encodings.newDefaultEncodingRegistry();

    // TODO This uses by default encoding type CL100K_BASE which might be a problem when using other LLM providers
    private static final Encoding encoding = encodingRegistry.getEncodingForModel(ModelType.GPT_4);

    public static int countTokens(String text) {
        return encoding.countTokens(text);
    }
}
