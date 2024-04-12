package com.devoxx.genie.service.splitter;

import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.segment.TextSegment;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.util.*;
import java.util.stream.Collectors;

public class HtmlSplitter implements DocumentSplitter {

    private final int maxWordsPerChunk;
    private final boolean greedilyAggregateSiblingNodes;
    private final Set<String> htmlTagsToExclude;
    private static final Set<String> sectionBreakHtmlTags = new HashSet<>(Arrays.asList(
        "article", "br", "div", "h1", "h2", "h3", "h4", "h5", "h6", "hr", "footer", "header", "main", "nav"
    ));

    public HtmlSplitter(Integer chunkSize) {
        this(chunkSize, true, new HashSet<>(Arrays.asList("noscript", "script", "style")));
    }

    public HtmlSplitter(int maxWordsPerChunk,
                        boolean greedilyAggregateSiblingNodes,
                        Set<String> htmlTagsToExclude) {
        this.maxWordsPerChunk = maxWordsPerChunk;
        this.greedilyAggregateSiblingNodes = greedilyAggregateSiblingNodes;
        this.htmlTagsToExclude = htmlTagsToExclude.stream().map(String::toLowerCase).collect(Collectors.toSet());
    }

    public List<String> chunk(String html) {
        Document doc = Jsoup.parse(html);
        List<String> passages = new ArrayList<>();
        chunkNode(doc.body(), new StringBuilder(), passages);
        return passages;
    }

    private void chunkNode(Element node, StringBuilder currentText, List<String> passages) {
        if (node == null || htmlTagsToExclude.contains(node.tagName().toLowerCase())) {
            return;
        }

        if (sectionBreakHtmlTags.contains(node.tagName().toLowerCase())) {
            addPassage(currentText, passages);
        }

        if (!node.ownText().isEmpty()) {
            String text = node.ownText().trim();
            if (!text.isEmpty()) {
                int currentWordCount = wordCount(currentText.toString());
                int additionalWordCount = wordCount(text);
                if (currentWordCount + additionalWordCount > this.maxWordsPerChunk) {
                    addPassage(currentText, passages);
                }
                if (!currentText.isEmpty()) {
                    currentText.append(" ");
                }
                currentText.append(text);
            }
        }

        for (Element child : node.children()) {
            chunkNode(child, currentText, passages);
        }

        if (!greedilyAggregateSiblingNodes) {
            addPassage(currentText, passages);
        }
    }

    private void addPassage(StringBuilder currentText, List<String> passages) {
        if (!currentText.isEmpty()) {
            passages.add(currentText.toString());
            currentText.setLength(0); // Reset the StringBuilder for the next passage
        }
    }

    private int wordCount(String text) {
        return text.isEmpty() ? 0 : text.split("\\s+").length;
    }

    @Override
    public List<TextSegment> split(dev.langchain4j.data.document.Document document) {
        String htmlContent = document.text();
        List<String> passages = chunk(htmlContent);
        List<TextSegment> textSegments = new ArrayList<>();
        passages.forEach(passage -> {
            TextSegment textSegment = new TextSegment(passage, document.metadata());
            textSegments.add(textSegment);
        });
        return textSegments;
    }

    @Override
    public List<TextSegment> splitAll(List<dev.langchain4j.data.document.Document> documents) {
        List<TextSegment> allTextSegments = new ArrayList<>();
        documents.forEach(document -> allTextSegments.addAll(split(document)));
        return allTextSegments;
    }
}
