package com.devoxx.genie.service.util.bm25.stemmer;

import com.devoxx.genie.service.util.bm25.Language;

/**
 * A language stemmer.
 */
public interface Stemmer {
    String stem(String word);
    Language getSupportedLanguage();
}
