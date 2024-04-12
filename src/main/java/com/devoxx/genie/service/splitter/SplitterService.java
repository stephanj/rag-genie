package com.devoxx.genie.service.splitter;

import com.devoxx.genie.domain.enumeration.SplitterStrategy;
import com.devoxx.genie.service.ContentService;
import com.devoxx.genie.service.dto.ContentDTO;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.segment.TextSegment;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SplitterService {
    private static final Logger LOGGER = LoggerFactory.getLogger(SplitterService.class);

    private final ContentService contentService;

    public SplitterService(ContentService contentService) {
        this.contentService = contentService;
    }

    /**
     * Process the content
     *
     * @param strategy     the splitter strategy
     * @param contentId    the content id
     * @param chunkSize    the text size
     * @param chunkOverlap the text overlap
     * @return the list of chunks
     */
    public List<String> split(SplitterStrategy strategy,
                              String contentId,
                              int chunkSize,
                              int chunkOverlap) {

        ContentDTO contentDTO = contentService.findById(Long.parseLong(contentId));
        LOGGER.debug("Splitting content: {}", contentDTO.getValue());

        DocumentSplitter splitter = DocumentSplitterFactory.getSplitter(strategy, chunkSize, chunkOverlap);
        List<TextSegment> split = splitter.split(new Document(contentDTO.getValue()));

        return List.of(split.stream().map(TextSegment::text).toArray(String[]::new));
    }
}
