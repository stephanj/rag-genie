package com.devoxx.genie.service.util.bm25.stemmer.snowball;

import com.devoxx.genie.service.util.bm25.Language;
import com.devoxx.genie.service.util.bm25.stemmer.Stemmer;
import org.tartarus.snowball.ext.englishStemmer;

public class EnglishStemmer implements Stemmer {
    private final englishStemmer instance = new englishStemmer();

    @Override
    public String stem(String word) {
        instance.setCurrent(word);
        if (instance.stem()) {
            return instance.getCurrent();
        } else {
            return word;
        }
    }

    @Override
    public Language getSupportedLanguage() {
        return Language.ENGLISH;
    }
}
