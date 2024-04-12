package com.devoxx.genie.util.bm25;

import com.devoxx.genie.service.util.bm25.BM25;
import com.devoxx.genie.service.util.bm25.stemmer.snowball.EnglishStemmer;
import com.devoxx.genie.service.util.bm25.stopwords.StopWords;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class BM25Test {

    @Test
    void testBM25_ILoveJava() {
        List<String> corpus = List.of(
            "I love programming",
            "Java is my favorite programming language",
            "I enjoy writing code in Java",
            "Java is another popular programming language",
            "I find programming fascinating",
            "I love Java",
            "I prefer Java over Python"
        );

        BM25 bm25 = new BM25(corpus);

        List<Map.Entry<Integer, Double>> results = bm25.search("I love java");

        for (Map.Entry<Integer, Double> entry : results) {
            System.out.println("Sentence " + entry.getKey() + " : Score = " + entry.getValue() + " - [" + corpus.get(entry.getKey()) + "]");
        }

        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(corpus.size());
        assertThat(results.getFirst().getKey()).isEqualTo(5);

        assertThat(results.getFirst().getValue()).isGreaterThan(1.8);
    }

    @Test
    void test_presentationTitles() {
        List<String> corpus = List.of(
            "Java 21",
            "10 Years of The Well-Grounded Java Developer",
            "IntelliJ Super Productivity in 45 Minutes",
            "Java Language update",
            "Teaching old Streams new tricks",
            "Ask the Java Architects",
            "Ask the Java Architects",
            "Optimize the world for fun and profit",
            "Quarkus Community BOF - Devoxx.be Edition",
            "Developer Unproductivity Horror Stories");

        BM25 bm25 = new BM25(corpus);
        List<Map.Entry<Integer, Double>> results = bm25.search("Java");
        for (Map.Entry<Integer, Double> entry : results) {
            System.out.println("Sentence " + entry.getKey() + " : Score = " + entry.getValue() + " - [" + corpus.get(entry.getKey()) + "]");
        }
    }

    @Test
    void test_jsonBased_corpus() {
        List<String> corpus = List.of(
            "{\"title\": \"Java 21\", \"speakers\": [{\"fullName\": \"Brian Goetz\", \"company\": \"Oracle\"}]}",
            "{\"title\": \"10 Years of The Well-Grounded Java Developer\", \"speakers\": [{\"fullName\": \"Ben Evans\", \"company\": \"Red Hat\"}]}",
            "{\"title\": \"IntelliJ Super Productivity in 45 Minutes\", \"speakers\": [{\"fullName\": \"Heinz Kabutz\", \"company\": \"JavaSpecialists.eu\"}]}",
            "{\"title\": \"Java Language update\", \"speakers\": [{\"fullName\": \"Brian Goetz\", \"company\": \"Oracle\"}]}",
            "{\"title\": \"Teaching old Streams new tricks\", \"speakers\": [{\"fullName\": \"Viktor Klang\", \"company\": \"Oracle\"}]}",
            "{\"title\": \"Ask the Java Architects\", \"speakers\": [{\"fullName\": \"Sharat Chander\", \"company\": \"Oracle, Corp\"}, {\"fullName\": \"Alan Bateman\", \"company\": \"Oracle\"}, {\"fullName\": \"Viktor Klang\", \"company\": \"Oracle\"}, {\"fullName\": \"Stuart Marks\", \"company\": \"Oracle\"}, {\"fullName\": \"Brian Goetz\", \"company\": \"Oracle\"}]}",
            "{\"title\": \"Optimize the world for fun and profit\", \"speakers\": [{\"fullName\": \"Geoffrey De Smet\", \"company\": \"Timefold\"}, {\"fullName\": \"Lukáš Petrovický\", \"company\": \"Timefold\"}]}",
            "{\"title\": \"Quarkus Community BOF - Devoxx.be Edition\", \"speakers\": [{\"fullName\": \"Dimitris Andreadis\", \"company\": \"Red Hat\"}]}",
            "{\"title\": \"Developer Unproductivity Horror Stories\", \"speakers\": [{\"fullName\": \"Trisha Gee\", \"company\": \"Gradle\"}, {\"fullName\": \"Helen Scott\", \"company\": \"JetBrains\"}]}"
        );
        List<String> concatenatedTexts = new ArrayList<>();
        Gson gson = new Gson();

        for (String jsonStr : corpus) {
            JsonElement element = gson.fromJson(jsonStr, JsonElement.class);
            StringBuilder concatenatedText = new StringBuilder();
            extractAndConcatTexts(element, concatenatedText);
            concatenatedTexts.add(concatenatedText.toString());
        }

        BM25 bm25 = new BM25(concatenatedTexts);

        List<Map.Entry<Integer, Double>> results = bm25.search("Java Brian Goetz");

        for (Map.Entry<Integer, Double> entry : results) {
            System.out.println("Sentence " + entry.getKey() + " : Score = " + entry.getValue() + " - [" + corpus.get(entry.getKey()) + "]");
        }
    }

    private static void extractAndConcatTexts(JsonElement element, StringBuilder concatenatedText) {
        if (element.isJsonPrimitive() && element.getAsJsonPrimitive().isString()) {
            concatenatedText.append(element.getAsString());
            concatenatedText.append(" "); // Add a space between each text for readability
        } else if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                extractAndConcatTexts(entry.getValue(), concatenatedText);
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            for (JsonElement el : array) {
                extractAndConcatTexts(el, concatenatedText);
            }
        }
    }

    @Test
    void testBM25_PythonProgramming() {
        List<String> corpus = List.of(
            "I love programming",
            "Java is my favorite programming language",
            "I enjoy writing code in Java",
            "Java is another popular programming language",
            "I find programming fascinating",
            "I love Java",
            "I prefer Java over Python"
        );

        BM25 bm25 = new BM25(corpus);

        List<Map.Entry<Integer, Double>> results = bm25.search("Python programming");
        for (Map.Entry<Integer, Double> entry : results) {
            System.out.println("Sentence " + entry.getKey() + " : Score = " + entry.getValue() + " - [" + corpus.get(entry.getKey()) + "]");
        }

        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(corpus.size());

        assertThat(results.getFirst().getKey()).isEqualTo(6);
        assertThat(results.getLast().getKey()).isEqualTo(5);

        assertThat(results.getFirst().getValue()).isGreaterThan(1.5);
        assertThat(results.getLast().getValue()).isEqualTo(0.0);
    }

    @Test
    void testBM25_withEnglishStopWords() {
        List<String> corpus = List.of(
            "I love programming",
            "Java is my favorite programming language",
            "I enjoy writing code in Java",
            "Java is another popular programming language",
            "I find programming fascinating",
            "I love Java",
            "I prefer Java over Python"
        );

        BM25 bm25 = new BM25(corpus, StopWords.ENGLISH);

        List<Map.Entry<Integer, Double>> results = bm25.search("Python programming");
        for (Map.Entry<Integer, Double> entry : results) {
            System.out.println("Sentence " + entry.getKey() + " : Score = " + entry.getValue() + " - [" + corpus.get(entry.getKey()) + "]");
        }

        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(corpus.size());

        assertThat(results.getFirst().getKey()).isEqualTo(6);
        assertThat(results.getLast().getKey()).isEqualTo(5);

        assertThat(results.getFirst().getValue()).isGreaterThan(1.5);
        assertThat(results.getLast().getValue()).isEqualTo(0.0);
    }

    @Test
    void testBM25_withEnglishStopWords_and_Stemmer() {
        List<String> corpus = List.of(
            "I love programming",
            "Java is my favorite programming language",
            "I enjoy writing code in Java",
            "Java is another popular programming language",
            "I find programming fascinating",
            "I love Java",
            "I prefer Java over Python"
        );

        BM25 bm25 = new BM25(corpus, StopWords.ENGLISH, new EnglishStemmer());

        List<Map.Entry<Integer, Double>> results = bm25.search("I love Java programming");
        for (Map.Entry<Integer, Double> entry : results) {
            System.out.println("Sentence " + entry.getKey() + " : Score = " + entry.getValue() + " - [" + corpus.get(entry.getKey()) + "]");
        }

        assertThat(results).isNotNull();
        assertThat(results.size()).isEqualTo(corpus.size());

        assertThat(results.getFirst().getKey()).isEqualTo(0);
        assertThat(results.getLast().getKey()).isEqualTo(2);

        assertThat(results.getFirst().getValue()).isGreaterThan(2.0);
    }
}
