package com.devoxx.genie.service;

import com.devoxx.genie.service.dto.EvaluationDTO;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@GenieServiceTest
class EvaluationServiceIT {

    @Autowired
    EvaluationService evaluationService;

    @Test
    void testSaveEvaluation() {
        EvaluationDTO evaluationDTO = new EvaluationDTO();
        evaluationDTO.setUserId(1L);
        evaluationDTO.setAnswer("Test answer");
        evaluationDTO.setQuestion("Test question");
        evaluationDTO.setName("Test name");
        evaluationDTO.setKeywords(List.of("test", "keywords"));

        EvaluationDTO saved = evaluationService.save(evaluationDTO);

        assertThat(saved.getId()).isNotNull();
    }
}
