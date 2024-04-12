package dev.langchain4j.model.cohere;

import dev.langchain4j.model.cohere.callback.AsyncCallback;
import dev.langchain4j.model.cohere.request.EmbedRequest;
import dev.langchain4j.model.cohere.request.RerankRequest;
import dev.langchain4j.model.cohere.response.EmbedResponse;
import dev.langchain4j.model.cohere.response.RerankResponse;
import lombok.Builder;

import java.time.Duration;

class CohereClient extends CohereExecute {

    private final CohereApi cohereApi;

    @Builder
    CohereClient(String baseUrl,
                 String apiKey,
                 Duration timeout,
                 boolean logRequests,
                 boolean logResponses) {
        cohereApi = new CohereApiFactory()
            .createHttpClient(baseUrl, apiKey, timeout, logRequests, logResponses)
            .createGson()
            .build();
    }

    public RerankResponse rerank(RerankRequest request) {
        return execute(cohereApi.rerank(request));
    }

    public EmbedResponse embed(EmbedRequest request) {
        return execute(cohereApi.embed(request));
    }

    public void embedAsync(EmbedRequest request, AsyncCallback<EmbedResponse> callback) {
        execute(cohereApi.embed(request), callback);
    }
}
