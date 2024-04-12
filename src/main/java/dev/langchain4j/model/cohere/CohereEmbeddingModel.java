package dev.langchain4j.model.cohere;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.cohere.request.EmbedRequest;
import dev.langchain4j.model.cohere.request.enumeration.CohereInputType;
import dev.langchain4j.model.cohere.request.enumeration.CohereLanguageModel;
import dev.langchain4j.model.cohere.response.EmbedResponse;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import lombok.Builder;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static dev.langchain4j.internal.Utils.getOrDefault;
import static dev.langchain4j.internal.Utils.isNullOrEmpty;
import static dev.langchain4j.internal.ValidationUtils.ensureNotBlank;
import static dev.langchain4j.model.cohere.CohereConfig.DEFAULT_BASE_URL;
import static java.time.Duration.ofSeconds;

/**
 * Cohere embedding model.
 * * <a href="https://docs.cohere.com/docs/embed">Cohere Embed API</a>.
 */
public class CohereEmbeddingModel implements EmbeddingModel {
    private final CohereClient client;
    private final CohereLanguageModel modelName;
    private final CohereInputType inputType;

    @Builder
    public CohereEmbeddingModel(String baseUrl,
                                String apiKey,
                                CohereLanguageModel modelName,
                                CohereInputType inputType,
                                Duration timeout,
                                Boolean logRequests,
                                Boolean logResponses) {
        this.client = CohereClient.builder()
            .baseUrl(isNullOrEmpty(baseUrl) ? DEFAULT_BASE_URL : baseUrl)
            .apiKey(ensureNotBlank(apiKey, "apiKey"))
            .timeout(getOrDefault(timeout, ofSeconds(60)))
            .logRequests(getOrDefault(logRequests, false))
            .logResponses(getOrDefault(logResponses, false))
            .build();

        this.modelName = getOrDefault(modelName, CohereLanguageModel.EMBED_ENGLISH_LIGHT_V3_0);

        this.inputType = getOrDefault(inputType, CohereInputType.SEARCH_DOCUMENT);
    }

    public static CohereEmbeddingModel withApiKey(String apiKey) {
        return CohereEmbeddingModel.builder().apiKey(apiKey).build();
    }

    @Override
    public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {

        String[] textArray = textSegments.stream()
            .map(TextSegment::text)
            .toArray(String[]::new);

        EmbedRequest request = EmbedRequest.builder()
            .texts(textArray)
            .input_type(inputType.toString())
            .model(this.modelName.getModelName())
            .build();

        EmbedResponse response = this.client.embed(request);

        return Response.from(convertResponseToEmbeddings(response));
    }

    public List<Embedding> convertResponseToEmbeddings(EmbedResponse response) {
        if (response.getEmbeddings() == null) {
            throw new IllegalArgumentException("Embeddings data cannot be null");
        }

        // Looks ugly, but not sure how to improve it with Float[] to float[] conversion.  Suggestions welcome!
        List<Embedding> embeddingList = new ArrayList<>();
        for (Float[] floatObjectArray : response.getEmbeddings()) {
            float[] primitiveArray = new float[floatObjectArray.length];
            for (int i = 0; i < floatObjectArray.length; i++) {
                Float floatValue = floatObjectArray[i];
                primitiveArray[i] = floatValue == null ? 0.0f : floatValue;
            }
            embeddingList.add(new Embedding(primitiveArray));
        }
        return embeddingList;
    }
}
