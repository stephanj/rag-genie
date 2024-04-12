package com.devoxx.genie.service.util.bm25.stopwords;

import com.devoxx.genie.service.util.bm25.Language;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Collectors;

public class StopWords {

    private static final Logger LOGGER = Logger.getLogger(StopWords.class.getName());

    public static final Set<String> ENGLISH = load(Language.ENGLISH);

    private StopWords() {
    }

    private static Set<String> load(Language language) {
        String filename = "stopwords-" + language.getCode() + ".txt";
        try (InputStream inputStream = StopWords.class.getClassLoader().getResourceAsStream(filename)) {
            if (inputStream == null) {
                LOGGER.severe("File not found: " + filename);
                return Set.of();
            }
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
                // Joining each line of the file content
                return reader.lines().collect(Collectors.toSet());
            }
        } catch (IOException e) {
            LOGGER.severe("Error reading file: " + filename);
            return Set.of();
        }
    }


}
