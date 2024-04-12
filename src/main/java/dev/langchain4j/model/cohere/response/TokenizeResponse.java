package dev.langchain4j.model.cohere.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TokenizeResponse {
    private Integer[] tokens;
    private String[] token_strings;
    private Meta meta;

    public Integer[] getTokens() {
        return tokens;
    }

    public Integer getToken(int index) {
        return tokens[index];
    }

    public String[] getTokenStrings() {
        return token_strings;
    }

    public String getTokenString(int index) {
        return token_strings[index];
    }
}
