package com.devoxx.genie.service;

import com.devoxx.genie.domain.EmbeddingModelReference;
import com.devoxx.genie.domain.User;
import com.devoxx.genie.domain.enumeration.LanguageModelType;
import com.devoxx.genie.repository.EmbeddingModelReferenceRepository;
import com.devoxx.genie.repository.UserRepository;
import com.devoxx.genie.service.dto.EmbeddingModelReferenceDTO;
import dev.langchain4j.model.embedding.EmbeddingModel;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@GenieServiceTest
class EmbeddingModelServiceIT {

    @Autowired
    EmbeddingModelService embeddingModelService;

    @Autowired
    EmbeddingModelReferenceRepository embeddingModelReferenceRepository;

    @Autowired
    UserRepository userRepository;

    @Test
    void testSave_and_findAll() {
        int originalSize = embeddingModelService.findAll().size();

        EmbeddingModelReference embeddingModelReference = new EmbeddingModelReference();
        embeddingModelReference.setName("test");
        embeddingModelReference.setDimSize(1024);
        embeddingModelReference.setApiKeyRequired(false);
        embeddingModelReference.setProvider(LanguageModelType.OLLAMA);
        embeddingModelReference.setCostUsage1m(0D);
        embeddingModelReference.setSlug("ola");
        embeddingModelReference.setMaxTokens(4048);
        EmbeddingModelReference saved = embeddingModelReferenceRepository.save(embeddingModelReference);
        assertThat(saved.getId()).isEqualTo(embeddingModelReference.getId());

        List<EmbeddingModelReferenceDTO> all = embeddingModelService.findAll();
        assertThat(all.size()).isGreaterThan(originalSize);
    }

    @Test
    void testGetEmbeddingModelReferenceDTOById() {
        EmbeddingModelReferenceDTO first = embeddingModelService.findAll().getFirst();

        EmbeddingModelReferenceDTO embeddingModelReferenceDTOById = embeddingModelService.getEmbeddingModelReferenceDTOById(first.getId());

        assertThat(embeddingModelReferenceDTOById.getId()).isEqualTo(first.getId());
    }

    @Test
    void testGetEmbeddingModelReferenceFromEmbedId() {
        EmbeddingModelReferenceDTO first = embeddingModelService.findAll().getFirst();

        EmbeddingModelReference embeddingModelReferenceFromEmbedId =
            embeddingModelService.getEmbeddingModelReferenceById(first.getId());

        assertThat(embeddingModelReferenceFromEmbedId.getId()).isEqualTo(first.getId());
    }

    @Test
    void testGetEmbeddingModelByUserIdAndRefId() {
        User user = userRepository.findAll().getFirst();
        EmbeddingModelReferenceDTO first = embeddingModelService.findAll().getFirst();

        Optional<EmbeddingModel> response =
            embeddingModelService.getEmbeddingModelByUserIdAndRefId(user.getId(), first.getId());

        assertThat(response.isPresent()).isTrue();
    }
}
