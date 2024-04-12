package dev.langchain4j.model.cohere.callback;

/**
 * Callback to use with Async API calls
 *
 * @param <T> parameter type of the response
 */
public interface AsyncCallback<T> {

    /**
     * Called when API response is successful
     *
     * @param response API response
     */
    void onSuccess(T response);

    /**
     * Called when API response fails
     *
     * @param throwable the exception with details about the failure
     */
    void onFailure(Throwable throwable);
}
