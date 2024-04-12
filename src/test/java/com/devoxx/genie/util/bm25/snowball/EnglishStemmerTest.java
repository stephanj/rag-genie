package com.devoxx.genie.util.bm25.snowball;

import com.devoxx.genie.service.util.bm25.stemmer.snowball.EnglishStemmer;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class EnglishStemmerTest {

    @Test
    void test_EnglishStemmer() {
        EnglishStemmer englishStemmer = new EnglishStemmer();
        String[] words = {"running", "ran", "run", "runner", "runs"};
        String[] expectedWords = {"run", "ran", "run", "runner", "run"};
        int index = 0;
        for (String word : words) {
            String stemmedWord = englishStemmer.stem(word);
            assertThat(stemmedWord).isEqualTo(expectedWords[index++]);
        }
    }
}
