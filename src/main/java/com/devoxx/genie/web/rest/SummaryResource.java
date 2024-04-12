package com.devoxx.genie.web.rest;

import com.devoxx.genie.service.LanguageModelService;
import com.devoxx.genie.service.dto.LanguageModelDTO;
import com.devoxx.genie.service.summary.SummaryService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SummaryResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(SummaryResource.class);

    private final LanguageModelService languageModelService;
    private final SummaryService summaryService;

    public SummaryResource(LanguageModelService languageModelService,
                           SummaryService summaryService) {
        this.languageModelService = languageModelService;
        this.summaryService = summaryService;
    }

    @PostMapping("/summary/basic/{modelId}/{temperature}")
    public ResponseEntity<List<String>> basicSummarization(@PathVariable Long modelId,
                                                           @PathVariable Double temperature,
                                                           @RequestBody String[] chunks) {

        LOGGER.debug("Create summaries");

        List<String> result = new ArrayList<>();

        LanguageModelDTO languageModel = languageModelService.findById(modelId);

        Arrays.stream(chunks)
            .forEach(chunk -> {
                String prompt = "Summarize: " + chunk;
                String summary = summaryService.summarize(languageModel, temperature, prompt);
                result.add(summary);
            });

        return ResponseEntity.ok().body(result);
    }

    @PostMapping("/summary/cod/{modelId}/{temperature}")
    public ResponseEntity<String> chainOfDensitySummarization(@PathVariable Long modelId,
                                                              @PathVariable Double temperature,
                                                              @RequestBody String[] chunks) {
        LOGGER.debug("Create CoD summaries");

        LanguageModelDTO languageModel = languageModelService.findById(modelId);

        String prompt = "Article: \n" +
            Arrays.toString(chunks) + """

                ------------------------
                You will generate increasingly concise, entity-dense summaries of the above Article.

                Repeat the following 2 steps 5 times.

                Step 1. Identify 1-3 informative Entities ("; " delimited) from the Article which are missing from the previously generated summary.
                Step 2. Write a new, denser summary of identical length which covers every entity and detail from the previous summary plus the Missing Entities.

                A Missing Entity is:
                - Relevant: to the main story.
                - Specific: descriptive yet concise (5 words or fewer).
                - Novel: not in the previous summary.
                - Faithful: present in the Article.
                - Anywhere: located anywhere in the Article.

                Guidelines:
                - The first summary should be long (4-5 sentences, ~80 words) yet highly non-specific, containing little information beyond the entities marked as missing. Use overly verbose language and fillers (e.g., "this article discusses") to reach ~80 words.
                - Make every word count: rewrite the previous summary to improve flow and make space for additional entities.
                - Make space with fusion, compression, and removal of uninformative phrases like "the article discusses".
                - The summaries should become highly dense and concise yet self-contained, e.g., easily understood without the Article.
                - Missing entities can appear anywhere in the new summary.
                - Never drop entities from the previous summary. If space cannot be made, add fewer new entities.

                Remember, use the exact same number of words for each summary.

                Answer in JSON. The JSON should be a list (length 5) of dictionaries whose keys are "missingEntities" and "denserSummary".

                e.g.

                    [
                      {
                        "missingEntities": "topic 1; topic 2; topic 3",
                        "denserSummary": "..."
                      },
                      {
                        "missingEntities": "topic 4; topic 5; topic 6",
                        "denserSummary": "..."
                      },
                      ...
                  ]
            """;

        String answer = summaryService.summarize(languageModel, temperature, prompt);

        return ResponseEntity.ok().body(answer);
    }

}
