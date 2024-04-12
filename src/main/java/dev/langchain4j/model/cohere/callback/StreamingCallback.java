package dev.langchain4j.model.cohere.callback;

/**
 * Callback to use with Streaming API calls
 *
 * @param <S> parameter type of the response
 */
public interface StreamingCallback<S> {

    /**
     * Called with a part of the stream response
     *
     * @param response API response
     */
    void onPart(S response);

    /**
     * Called when stream of responses from API is complete
     *
     * @param response API response
     */
    void onComplete(S response);

    /**
     * Called when API response fails
     *
     * @param throwable the exception with details about the failure
     */
    void onFailure(Throwable throwable);
}
