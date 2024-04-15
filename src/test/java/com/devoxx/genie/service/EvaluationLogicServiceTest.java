package com.devoxx.genie.service;

import com.devoxx.genie.service.dto.EvaluationDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EvaluationLogicServiceTest {

    @Test
    void test_keywords_100_Match() {
        EvaluationLogicService evaluationLogicService =
            new EvaluationLogicService(null, null, null, null, null);

        var evaluationDTO = new EvaluationDTO();

        String answer = """
            * Combi ticket (5 days, 7 – 11 Oct. 2024) = 975 Euro
            * Conference ticket (3 days, 8 – 11 Oct. 2024) = 675 Euro
            * Deep-Dive ticket (2 days, 7 – 8 Oct. 2024) = 425 Euro
            """;

        List<String> keywords = List.of("975", "675", "425", "combi", "deep-dive");

        System.out.println("keywords: " + keywords);

        evaluationDTO.setKeywords(keywords);

        Double keywordMatch = evaluationLogicService.getKeywordMatch(evaluationDTO, "975, 675, 425, combi, deep-dive, VAT");

        assertThat(keywordMatch).isEqualTo(100.0);
    }

    @Test
    void test_keyword_88_Match() {
        EvaluationLogicService evaluationLogicService =
            new EvaluationLogicService(null, null, null, null, null);

        var evaluationDTO = new EvaluationDTO();

        String answer = """
            * Combi ticket (5 days, 7 – 11 Oct. 2024) = 975 Euro
            * Conference ticket (3 days, 8 – 11 Oct. 2024) = 675 Euro
            * Deep-Dive ticket (2 days, 7 – 8 Oct. 2024) = 425 Euro
            """;

        List<String> keywords = List.of("975", "675", "425", "combi", "deep-dive", "VAT");

        System.out.println("keywords: " + keywords);

        evaluationDTO.setKeywords(keywords);

        Double keywordMatch = evaluationLogicService.getKeywordMatch(evaluationDTO, "975, 675, 425, combi");

        assertThat(keywordMatch).isEqualTo(100.0);
    }
}
