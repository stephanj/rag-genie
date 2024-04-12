package com.devoxx.genie.service.embedding;

import com.devoxx.genie.service.embedding.cohere.CohereEmbedModel;
import com.devoxx.genie.service.embedding.cohere.CohereEmbeddingType;
import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.output.Response;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class CohereEmbedModelTest {

    @Test
    void embed_text_to_float() {
        CohereEmbedModel cohereEmbedModel =
            new CohereEmbedModel("embed-english-light-v3.0",
                CohereEmbeddingType.FLOAT,
                "xRFZKpERWOd2vVXftX69aBBeenWjBap710IKbBYw",     // Trial key
                10L);
        Response<Embedding> embed = cohereEmbedModel.embed("hello");
        Embedding content = embed.content();
        System.out.println(content);
        assertThat(content).isNotNull();
    }
}
