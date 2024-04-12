package com.devoxx.genie.service.embedding.cohere;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.data.segment.TextSegment;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Arrays;
import java.util.List;

import static dev.langchain4j.internal.Utils.getOrDefault;

public class CohereEmbedModel implements EmbeddingModel {
    private static final Logger LOGGER = LoggerFactory.getLogger(CohereEmbedModel.class);
    private static final String BASE_URL = "https://api.cohere.ai/v1/embed";
    private static final int FLOAT_DIMENSION_SIZE = 384;
    private static final int BINARY_DIMENSION_SIZE = 48;
    private final HttpClient httpClient;
    private final JsonObject requestBody;
    private final String modelName;
    private final String apiKey;
    private final CohereEmbeddingType[] embeddingTypes;

    /**
     * Creates a new instance of the CohereEmbedModel.
     *
     * @param modelName      The name of the model to use.
     * @param embeddingType  The type of embedding to use.
     * @param apiKey         The API key to use for authentication.
     * @param timeoutSeconds The timeout in seconds for the HTTP client.
     */
    public CohereEmbedModel(String modelName,
                            CohereEmbeddingType embeddingType,
                            String apiKey,
                            Long timeoutSeconds) {
        this(modelName, new CohereEmbeddingType[]{embeddingType}, apiKey, timeoutSeconds);
    }

    /**
     * Creates a new instance of the CohereEmbedModel.
     *
     * @param modelName      The name of the model to use.
     * @param embeddingTypes The types of embedding to use.
     * @param apiKey         The API key to use for authentication.
     * @param timeoutSeconds The timeout in seconds for the HTTP client.
     */
    public CohereEmbedModel(String modelName,
                            CohereEmbeddingType[] embeddingTypes,
                            String apiKey,
                            Long timeoutSeconds) {

        if (apiKey == null) {
            throw new IllegalArgumentException("API key is required");
        }
        this.apiKey = apiKey;

        if (modelName == null) {
            throw new IllegalArgumentException("Model name is required");
        }
        this.modelName = modelName;

        this.embeddingTypes = embeddingTypes;

        this.httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(getOrDefault(timeoutSeconds, 60L)))
            .build();

        requestBody = new JsonObject();
        requestBody.addProperty("input_type", "classification");
    }

    @Override
    public Response<List<Embedding>> embedAll(List<TextSegment> textSegments) {
        LOGGER.debug("Embedding {} text segments", textSegments.size());

        JsonArray textArray = getTextToEmbed(textSegments);

        JsonArray typeArray = new JsonArray();
        Arrays.stream(embeddingTypes).forEach(type -> typeArray.add(type.toString()));

        requestBody.addProperty("model", modelName);
        requestBody.add("embedding_types", typeArray);
        requestBody.add("texts", textArray);

        HttpRequest httpRequest = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL))
            .header("Authorization", "Bearer " + apiKey)
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(requestBody.toString()))
            .build();

        return sendRequest(httpRequest);
    }

    @NotNull
    private JsonArray getTextToEmbed(List<TextSegment> textSegments) {
        return textSegments.stream()
            .map(TextSegment::text)
            .collect(JsonArray::new, JsonArray::add, JsonArray::addAll);
    }

    private Response<List<Embedding>> sendRequest(HttpRequest request) {
        try {
            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() != 200) {
                LOGGER.error("Failed to embed text segments. Response status code: {}", response.statusCode());
                return null; // Consider throwing an exception or returning an error response instead.
            }

            JsonObject jsonResponse = JsonParser.parseString(response.body()).getAsJsonObject();
            String responseType = jsonResponse.get("response_type").getAsString();

            List<Embedding> embeddings;

            if (responseType.equalsIgnoreCase("embeddings_by_type")) {
                LOGGER.debug("Response type is embeddings_by_type");
                String embeddingType = embeddingTypes[0].toString().toLowerCase();
                JsonArray embeddingsJson = jsonResponse.get("embeddings").getAsJsonObject().get(embeddingType).getAsJsonArray();
                embeddings = parseEmbeddings(embeddingsJson, embeddingTypes[0]);
            } else {
                LOGGER.error("Response type is not embeddings_by_type : {}", responseType);
                JsonArray embeddingsJson = jsonResponse.getAsJsonArray("embeddings");
                embeddings = parseEmbeddings(embeddingsJson, CohereEmbeddingType.FLOAT);
            }

            return new Response<>(embeddings);
        } catch (Exception e) {
            LOGGER.error("Failed to send request", e);
            return null; // Consider handling this case more gracefully.
        }
    }

    private List<Embedding> parseEmbeddings(JsonArray embeddingsJson,
                                            CohereEmbeddingType embeddingType) {
        return embeddingsJson.asList().stream()
            .map(element ->
                switch (embeddingType) {
                    case FLOAT -> parseFloatEmbedding(element);
                    case INT8 -> parseIntEmbedding(element);
                    case BINARY -> parseBinaryEmbedding(element);
                    default -> throw new IllegalArgumentException("Unknown embedding type: " + embeddingType);
                })
            .toList();
    }

    private Embedding parseFloatEmbedding(JsonElement jsonElement) {
        float[] embedding = new float[FLOAT_DIMENSION_SIZE];
        JsonArray jsonEmbedding = jsonElement.getAsJsonArray();
        for (int i = 0; i < FLOAT_DIMENSION_SIZE; i++) {
            embedding[i] = jsonEmbedding.get(i).getAsFloat();
        }
        return new Embedding(embedding);
    }

    private Embedding parseIntEmbedding(JsonElement jsonElement) {
        float[] embedding = new float[FLOAT_DIMENSION_SIZE];
        JsonArray jsonEmbedding = jsonElement.getAsJsonArray();
        for (int i = 0; i < FLOAT_DIMENSION_SIZE; i++) {
            embedding[i] = jsonEmbedding.get(i).getAsInt();
        }
        return new Embedding(embedding);
    }

    private Embedding parseBinaryEmbedding(JsonElement jsonElement) {
        float[] embedding = new float[FLOAT_DIMENSION_SIZE];
        JsonArray jsonEmbedding = jsonElement.getAsJsonArray();
        for (int i = 0; i < BINARY_DIMENSION_SIZE; i++) {
            embedding[i] = jsonEmbedding.get(i).getAsByte();
        }
        // fill the rest of the embedding with zeros
        for (int i = BINARY_DIMENSION_SIZE; i < FLOAT_DIMENSION_SIZE; i++) {
            embedding[i] = 0.0f;
        }
        return new Embedding(embedding);
    }
}
