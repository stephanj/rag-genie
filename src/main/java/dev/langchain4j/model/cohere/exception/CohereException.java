package dev.langchain4j.model.cohere.exception;

import com.google.gson.Gson;

import java.io.IOException;

public class CohereException extends RuntimeException {
    private static final Gson gson = new Gson();

    /**
     * Status code of the response
     */
    int code;

    /**
     * CohereException constructor for errors from Cohere API
     *
     * @param code    status code
     * @param message error message
     */
    CohereException(int code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * CohereException constructor when the error is local
     *
     * @param cause an exception
     */
    public CohereException(Throwable cause) {
        super(cause);
    }

    /**
     * Parse exceptions:
     * status code: 429; body: {"message":"You are using a Trial key, which is limited to 5 API calls / minute.
     * You can continue to use the Trial key for free or upgrade to a Production key with higher rate limits at
     * 'https://dashboard.cohere.ai/api-keys'.
     * Contact us on 'https://discord.gg/XW44jPfYJu' or email us at support@cohere.com with any questions"}
     * status code: 401; body: {"message":"invalid api token"}
     *
     * @param response the API response with error message
     */
    public static CohereException fromResponse(retrofit2.Response<?> response) {
        try {
            int code = response.code();
            String body = response.errorBody().string();
            CohereResponse resp = gson.fromJson(body, CohereResponse.class);
            return new CohereException(code, resp.message);
        } catch (IOException e) {
            return new CohereException(e);
        }
    }
}

