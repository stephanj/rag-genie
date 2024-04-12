package dev.langchain4j.model.cohere;

import dev.langchain4j.model.cohere.request.*;
import dev.langchain4j.model.cohere.response.*;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Streaming;

interface CohereApi {

    /**
     * This endpoint generates realistic text conditioned on a given input.
     *
     * @param request text generation request
     * @return tokenization response
     */
    @POST("/v1/generate")
    @Headers({"accept: application/json", "content-type: application/json"})
    Call<GenerateResponse>
    generate(@Body GenerateRequest request);

    /**
     * This streaming endpoint generates realistic text conditioned on a given input.
     *
     * @param request text generation request
     * @return a string representing a stream of text generation responses (one per line)
     */
    @Streaming
    @POST("/v1/generate")
    @Headers({"accept: application/stream+json", "content-type: application/json"})
    Call<String>
    generateStream(@Body GenerateRequest request);

    /**
     * This endpoint returns text embeddings. An embedding is a list of floating point numbers that captures semantic
     * information about the text that it represents.
     * Embeddings can be used to create text classifiers as well as empower semantic search.
     * To learn more about embeddings, see the embedding page.
     * If you want to learn more how to use the embedding model, have a look at the Semantic Search Guide
     *
     * @param request text embedding request
     * @return embeddings response
     */
    @POST("/v1/embed")
    @Headers({"accept: application/json", "content-type: application/json"})
    Call<EmbedResponse>
    embed(@Body EmbedRequest request);

    @POST("/v1/embed")
    @Headers({"accept: application/json", "content-type: application/json"})
    Call<TypedEmbedResponse>
    typedEmbed(@Body TypedEmbedRequest request);

    /**
     * This endpoint makes a prediction about which label fits the specified text inputs best. To make a prediction,
     * Classify uses the provided examples of text + label pairs as a reference.
     * Note: Custom Models trained on classification examples don't require the examples parameter to be passed in explicitly.
     *
     * @param request text classification request
     * @return classification response
     */
    @POST("/v1/classify")
    @Headers({"accept: application/json", "content-type: application/json"})
    Call<ClassifyResponse>
    classify(@Body ClassifyRequest request);

    /**
     * This endpoint splits input text into smaller units called tokens using byte-pair encoding (BPE).
     * To learn more about tokenization and byte pair encoding, see the tokens page.
     *
     * @param request text tokenization request
     * @return tokenization response
     */
    @POST("/v1/tokenize")
    @Headers({"accept: application/json", "content-type: application/json"})
    Call<TokenizeResponse>
    tokenize(@Body TokenizeRequest request);

    /**
     * This endpoint takes tokens using byte-pair encoding and returns their text representation.
     * To learn more about tokenization and byte pair encoding, see the tokens page.
     *
     * @param request token IDs de-tokenization request
     * @return de-tokenization response
     */
    @POST("/v1/detokenize")
    @Headers({"accept: application/json", "content-type: application/json"})
    Call<DetokenizeResponse>
    detokenize(@Body DetokenizeRequest request);

    /**
     * This endpoint identifies which language each of the provided texts is written in.
     *
     * @param request language detection request
     * @return language detection response
     */
    @POST("/v1/detect-language")
    @Headers({"accept: application/json", "content-type: application/json"})
    Call<DetectLanguageResponse>
    detectLanguage(@Body DetectLanguageRequest request);

    /**
     * This endpoint generates a summary in English for a given text.
     *
     * @param request Summarization request
     * @return Summarization response
     */
    @POST("/v1/summarize")
    @Headers({"accept: application/json", "content-type: application/json"})
    Call<SummarizeResponse>
    summarize(@Body SummarizeRequest request);

    /**
     * This endpoint takes in a query and a list of texts and produces an ordered array with each text assigned a
     * relevance score.
     *
     * @param request rerank request
     * @return rerank response
     */
    @POST("/v1/rerank")
    @Headers({"accept: application/json", "content-type: application/json"})
    Call<RerankResponse>
    rerank(@Body RerankRequest request);
}
