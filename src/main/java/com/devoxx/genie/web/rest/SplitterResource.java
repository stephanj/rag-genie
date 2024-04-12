package com.devoxx.genie.web.rest;

import com.devoxx.genie.domain.enumeration.SplitterStrategy;
import com.devoxx.genie.service.splitter.SplitterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@RequestMapping("/api")
public class SplitterResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(SplitterResource.class);

    private final SplitterService splitterService;

    public SplitterResource(SplitterService splitterService) {
        this.splitterService = splitterService;
    }

    @GetMapping("/splitter")
    public ResponseEntity<List<String>> splitText(
        @RequestParam(defaultValue = "RECURSIVE") SplitterStrategy strategy,
        @RequestParam() String contentIds,
        @RequestParam(required = false, defaultValue = "450") int chunkSize,
        @RequestParam(required = false, defaultValue = "25") int chunkOverlap) {

        LOGGER.debug("Getting splitter info with chunkSize : {} and chunkOverlap : {}", chunkSize, chunkOverlap);

        List<String> result = new ArrayList<>();

        Arrays.stream(contentIds.split(","))
            .forEach(contentId -> {
                List<String> chunks = splitterService.split(strategy, contentId, chunkSize, chunkOverlap);
                result.addAll(chunks);
            });

        return ResponseEntity.ok().body(result);
    }
}
