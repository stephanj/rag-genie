package dev.langchain4j.model.cohere.response;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedTreeMap;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TypedEmbedResponse {

    private String id;

    private String[] texts;

    private JsonObject embeddings;

    private Meta meta;

    public static class Embeddings {
        LinkedTreeMap<String, JsonArray> members;
    }
}
