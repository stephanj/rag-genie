package com.devoxx.genie.service.splitter;

import com.devoxx.genie.domain.enumeration.SplitterStrategy;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.splitter.*;

public class DocumentSplitterFactory {

    private DocumentSplitterFactory() {
    }

    /**
     * Get the splitter based on the strategy
     *
     * @param strategy     the splitter strategy
     * @param value        the regular expression value
     * @param chunkSize    the chunk size
     * @param chunkOverlap the chunk overlap
     * @return the document splitter
     */
    public static DocumentSplitter getSplitter(SplitterStrategy strategy,
                                               String value,
                                               int chunkSize,
                                               int chunkOverlap) {
        return switch (strategy) {
            case CHARACTER -> new DocumentByCharacterSplitter(chunkSize, chunkOverlap);
            case WORD -> new DocumentByWordSplitter(chunkSize, chunkOverlap);
            case SENTENCE -> new DocumentBySentenceSplitter(chunkSize, chunkOverlap);
            case LINE -> new DocumentByLineSplitter(chunkSize, chunkOverlap);
            case PARAGRAPH -> new DocumentByParagraphSplitter(chunkSize, chunkOverlap);
            case RECURSIVE -> DocumentSplitters.recursive(chunkSize, chunkOverlap);
            case JSON -> new DocumentByJSONSplitter();
            case REGEX -> new DocumentByRegexSplitter(value, "|", chunkSize, chunkOverlap, DocumentSplitters.recursive(chunkSize, chunkOverlap));
            default -> throw new IllegalArgumentException("Invalid strategy");
        };
    }
}
