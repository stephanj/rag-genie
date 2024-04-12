package com.devoxx.genie.service.util.bm25;

import com.devoxx.genie.service.util.bm25.stemmer.Stemmer;
import com.devoxx.genie.service.util.bm25.stemmer.snowball.EnglishStemmer;
import com.devoxx.genie.service.util.bm25.stopwords.StopWords;

import java.util.*;
import java.util.function.Function;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * BM25 (Best Matching 25) is a ranking function used by search engines to rank matching documents
 * according to their relevance to a given search query.
 * @link <a href="https://en.wikipedia.org/wiki/Okapi_BM25">Wikipedia</a>
 */
public class BM25 {

    private static final Pattern SPACE_PATTERN = Pattern.compile("\\s+");
    private final Set<String> stopWords;
    private final Stemmer stemmer;
    private final List<String> corpus; // List of documents
    private final double avgDocLength;
    private Map<Integer, Map<String, Integer>> tf; // Term Frequency
    private Map<String, Double> idf; // Inverse Document Frequency
    private final double termFrequencyScalingFactor;
    private final double documentLengthNormalizationFactor;

    /**
     * Constructor to initialize BM25
     * @param corpus list of documents
     */
    public BM25(final List<String> corpus) {
        this(corpus, 1.5, 0.75, StopWords.ENGLISH, new EnglishStemmer());
    }

    /**
     * Constructor to initialize BM25
     * @param corpus list of documents
     * @param stopWords set of stop words
     */
    public BM25(final List<String> corpus, Set<String> stopWords) {
        this(corpus, 1.5, 0.75, stopWords, new EnglishStemmer());
    }

    /**
     * Constructor to initialize BM25
     * @param corpus list of documents
     * @param stopWords set of stop words
     * @param stemmer stemmer
     */
    public BM25(final List<String> corpus, Set<String> stopWords, Stemmer stemmer) {
        this(corpus, 1.5, 0.75, stopWords, stemmer);
    }

    /**
     * Constructor to initialize BM25
     * @param corpus list of documents
     * @param termFrequencyScalingFactor scaling factor for term frequency
     * @param documentLengthNormalizationFactor normalization factor for document length
     */
    public BM25(final List<String> corpus,
                final double termFrequencyScalingFactor,
                final double documentLengthNormalizationFactor) {
        this(corpus,
            termFrequencyScalingFactor,
            documentLengthNormalizationFactor,
            StopWords.ENGLISH,
            new EnglishStemmer());
    }

    /**
     * Constructor to initialize BM25
     * @param corpus list of documents
     * @param termFrequencyScalingFactor scaling factor for term frequency
     * @param documentLengthNormalizationFactor normalization factor for document length
     * @param stopWords set of stop words
     * @param stemmer stemmer
     */
    public BM25(final List<String> corpus,
                final double termFrequencyScalingFactor,
                final double documentLengthNormalizationFactor,
                final Set<String> stopWords,
                final Stemmer stemmer) {
        if (corpus == null || corpus.isEmpty()) {
            throw new IllegalArgumentException("Corpus must not be null and must contain at least one document.");
        }
        if (termFrequencyScalingFactor <= 0 || documentLengthNormalizationFactor < 0) {
            throw new IllegalArgumentException("termFrequencyScalingFactor and documentLengthNormalizationFactor must be positive.");
        }
        this.corpus = corpus;
        this.stopWords = stopWords;
        this.stemmer = stemmer;
        this.avgDocLength = calculateAverageDocumentLength(corpus);
        this.tf = new HashMap<>();
        this.idf = new HashMap<>();
        this.termFrequencyScalingFactor = termFrequencyScalingFactor;
        this.documentLengthNormalizationFactor = documentLengthNormalizationFactor;
        initialize();
    }

    /**
     * Calculate the average length of documents in the corpus
     * @param corpus list of documents
     * @return average length of documents
     */
    private double calculateAverageDocumentLength(List<String> corpus) {
        long totalLength = corpus.stream()
            .mapToInt(doc -> SPACE_PATTERN.split(doc).length)
            .sum();
        return corpus.isEmpty() ? 0 : (double) totalLength / corpus.size();
    }

    /**
     * Initialize term frequency and inverse document frequency
     */
    private void initialize() {
        Map<String, Set<Integer>> docFreq = docFrequencyCalculation();
        termFrequencyCalculation();
        idfCalculation(docFreq);
    }

    /**
     * Calculate inverse document frequency (idf)
     * @param docFreq document frequency
     */
    private void idfCalculation(Map<String, Set<Integer>> docFreq) {
        int corpusSize = corpus.size();
        idf = docFreq.entrySet().stream()
            .collect(Collectors.toMap(
                Map.Entry::getKey,
                entry -> Math.log((corpusSize - entry.getValue().size() + 0.5) / (entry.getValue().size() + 0.5) + 1)
            ));
    }

    /**
     * The term frequency (tf) calculation.
     */
    private void termFrequencyCalculation() {
        tf = IntStream.range(0, corpus.size())
            .boxed()
            .collect(Collectors.toMap(
                Function.identity(),
                docIndex -> {
                    String[] terms = SPACE_PATTERN.split(corpus.get(docIndex).toLowerCase());
                    return Arrays.stream(terms)
                        .filter(term -> !stopWords.contains(term))
                        .map(stemmer::stem)
                        .collect(Collectors.groupingBy(Function.identity(), Collectors.summingInt(term -> 1)));
                }
            ));
    }

    /**
     * Calculate document frequency (docFreq)
     * @return document frequency
     */
    private Map<String, Set<Integer>> docFrequencyCalculation() {
        // Document Frequency (docFreq) calculation
        return IntStream.range(0, corpus.size())
            .boxed()
            .flatMap(docIndex -> {
                String[] terms = SPACE_PATTERN.split(corpus.get(docIndex).toLowerCase());
                return Arrays.stream(terms)
                    .distinct()
                    .map(term -> new AbstractMap.SimpleEntry<>(stemmer.stem(term), docIndex));
            })
            .collect(Collectors.groupingBy(
                Map.Entry::getKey,
                Collectors.mapping(Map.Entry::getValue, Collectors.toSet())
            ));
    }

    /**
     * Compute BM25 score for a document
     * @param docIndex index of the document
     * @param query list of query terms
     * @return BM25 score for the document
     */
    public double calculateDocumentScore(Integer docIndex, List<String> query) {
        String document = corpus.get(docIndex);
        double docLength = SPACE_PATTERN.split(document).length;
        return query.stream()
            .distinct()
            .mapToDouble(term -> calculateTermScore(docIndex, term, docLength))
            .sum();
    }

    /**
     * Calculate BM25 score for a term in a document
     * @param docIndex index of the document
     * @param term term to calculate score for
     * @param docLength length of the document
     * @return BM25 score for the term in the document
     */
    private double calculateTermScore(Integer docIndex, String term, double docLength) {
        double termFrequency = tf.get(docIndex).getOrDefault(term, 0);
        double idfValue = idf.getOrDefault(term, 0.0);
        if (idfValue == 0.0) {
            return 0.0; // Skipping term or handle differently
        }
        double numerator = idfValue * (termFrequency * (termFrequencyScalingFactor + 1));
        double denominator = termFrequency +
            termFrequencyScalingFactor *
            (1 - documentLengthNormalizationFactor + documentLengthNormalizationFactor * (docLength / avgDocLength));
        return numerator / denominator;
    }

    /**
     * Search for documents that match the query
     * @param query list of query terms
     * @return list of documents with their BM25 scores
     */
    public List<Map.Entry<Integer, Double>> search(String query) {
        if (query == null || query.isEmpty()) {
            throw new IllegalArgumentException("Query must not be null and must contain at least one term.");
        }

        List<String> queryTerms = Arrays.stream(SPACE_PATTERN.split(query.toLowerCase()))
            .filter(term -> !stopWords.contains(term))  // Filter out stop words
            .map(stemmer::stem)                         // Stem the query terms
            .toList();

        return IntStream.range(0, corpus.size())
            .boxed()
            .map(docIndex -> Map.entry(docIndex, calculateDocumentScore(docIndex, queryTerms)))
            .sorted(Map.Entry.<Integer, Double>comparingByValue().reversed())
            .toList();
    }

    /**
     * Main method to test the BM25 class
     * @param args command line arguments
     */
    public static void main(String[] args) {

        Long start = System.currentTimeMillis();

        List<String> corpus = List.of(
            "I love programming",
            "Java is my favorite programming language",
            "I enjoy writing code in Java",
            "Java is another popular programming language",
            "I find programming fascinating",
            "I love Java",
            "I prefer Java over Python"
        );

        double termFrequencyScalingFactor = 1.5;
        double documentLengthNormalizationFactor = 0.75;

        BM25 bm25 = new BM25(corpus,
                             termFrequencyScalingFactor,
                             documentLengthNormalizationFactor);

        String query = "I Love Java";

        try {
            List<Map.Entry<Integer, Double>> results = bm25.search(query);

            System.out.println("Search results for : " + query);

            for (Map.Entry<Integer, Double> entry : results) {
                System.out.println("Sentence " + entry.getKey() + " : Score = " + entry.getValue() + " - [" + corpus.get(entry.getKey()) + "]");
            }
        } catch (Exception e) {
            System.err.println("An error occurred: " + e.getMessage());
            e.printStackTrace();
        }

        Long end = System.currentTimeMillis();
        System.out.println("Time taken: " + (end - start) + "ms");
    }
}
