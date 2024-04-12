package com.devoxx.genie.util.bm25.stopwords;

import com.devoxx.genie.service.util.bm25.stopwords.StopWords;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertTrue;

class StopWordsTest {

    @Test
    void testEnglishStopWords() {
        Set<String> englishStopWords = StopWords.ENGLISH;
        boolean foundAbove = englishStopWords.contains("above");
        assertTrue(foundAbove);
    }
}
