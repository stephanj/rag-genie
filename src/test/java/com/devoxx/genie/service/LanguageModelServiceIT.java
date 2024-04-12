package com.devoxx.genie.service;

import com.devoxx.genie.domain.LanguageModel;
import com.devoxx.genie.repository.LanguageModelRepository;
import com.devoxx.genie.service.dto.LanguageModelDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@GenieServiceTest
class LanguageModelServiceIT {

    @Autowired
    LanguageModelService languageModelService;

    @Autowired
    LanguageModelRepository languageModelRepository;

    @Test
    void testSave() {
        long count = languageModelRepository.count();
        LanguageModelDTO languageModelDTO = new LanguageModelDTO();
        languageModelDTO.setName("test");
        languageModelDTO.setDescription("test");
        languageModelDTO.setCostInput1M(0D);
        languageModelDTO.setCostOutput1M(0D);
        languageModelDTO.setTokens(true);
        languageModelDTO.setWebsite("test");
        languageModelDTO.setApiKeyRequired(false);
        LanguageModelDTO saved = languageModelService.save(languageModelDTO);
        assertThat(saved.getId()).isNotNull();
        assertThat(languageModelRepository.count()).isEqualTo(count + 1);
    }

    @Test
    void testFindById() {
        long count = languageModelRepository.count();
        LanguageModelDTO languageModelDTO = new LanguageModelDTO();
        languageModelDTO.setName("test");
        languageModelDTO.setCostInput1M(0D);
        languageModelDTO.setCostOutput1M(0D);
        languageModelDTO.setApiKeyRequired(true);
        LanguageModelDTO saved = languageModelService.save(languageModelDTO);
        assertThat(saved.getId()).isNotNull();
        assertThat(languageModelRepository.count()).isEqualTo(count + 1);

        LanguageModelDTO byId = languageModelService.findById(saved.getId());
        assertThat(byId.getId()).isEqualTo(saved.getId());
    }

    @Test
    void testFindAll() {
        long count = languageModelRepository.count();
        Page<LanguageModelDTO> all = languageModelService.findAll(Pageable.unpaged());
        assertThat(all).isNotNull();
        assertThat(all.getTotalElements()).isEqualTo(count);
    }

    @Test
    void findByName() {
        LanguageModel first = languageModelRepository.findAll().getFirst();

        LanguageModelDTO byName = languageModelService.findByName(first.getName()).orElseThrow();
        assertThat(byName.getId()).isEqualTo(first.getId());
        assertThat(byName.getName()).isEqualTo(first.getName());
    }

    @Test
    void deleteById() {
        long count = languageModelRepository.count();
        LanguageModel first = languageModelRepository.findAll().getFirst();

        languageModelService.deleteById(first.getId())
            .orElseThrow(() -> new RuntimeException("LanguageModel not found"));

        assertThat(languageModelRepository.count()).isEqualTo(count - 1);
    }
}
