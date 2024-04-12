package com.devoxx.genie.service.score;

import dev.langchain4j.data.embedding.Embedding;
import dev.langchain4j.model.embedding.AllMiniLmL6V2EmbeddingModel;
import dev.langchain4j.model.embedding.EmbeddingModel;
import dev.langchain4j.model.output.Response;

/**
 * how similar the meaning of the texts is, rather than just their literal similarity),
 * techniques such as cosine similarity with TF-IDF vectors or using embeddings from models like BERT are often employed
 */
public class SimilarityScore {

    private SimilarityScore() {
    }

    private static final EmbeddingModel embeddingModel = new AllMiniLmL6V2EmbeddingModel();

    public static Double computeSimilarityScore(String referenceSentence, String candidateSentence) {

        Response<Embedding> referenceEmbed = embeddingModel.embed(referenceSentence);
        Embedding referenceEmbedding = referenceEmbed.content();

        Response<Embedding> candidateEmbed = embeddingModel.embed(candidateSentence);
        Embedding candidateEmbedding = candidateEmbed.content();


        // compare cosine similarity between the two embeddings
        return cosine(referenceEmbedding.vector(), 0,
            candidateEmbedding.vector(), 0,
            referenceEmbedding.dimension());
    }

    public static double cosine(float[] a, int aOffset, float[] b, int bOffset, int length) {
        float sum = 0.0f;
        float norm1 = 0.0f;
        float norm2 = 0.0f;
        for (int i = 0; i < length; i++) {
            float elem1 = a[aOffset + i];
            float elem2 = b[(bOffset + i)];
            sum += elem1 * elem2;
            norm1 += elem1 * elem1;
            norm2 += elem2 * elem2;
        }
        var percent = (float) (sum / Math.sqrt((double) norm1 * (double) norm2)) * 100;
        return Math.round(percent * 100.0) / 100.0f;
    }
}
