package com.devoxx.genie.service.splitter;

import com.google.gson.JsonArray;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentSplitter;
import dev.langchain4j.data.document.Metadata;
import dev.langchain4j.data.segment.TextSegment;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

public class DocumentByJSONSplitter implements DocumentSplitter {

    @Override
    public List<TextSegment> split(Document document) {
        String text = document.text();

        JsonReader reader = new JsonReader(new StringReader(text));

        JsonArray jsonArray = JsonParser.parseReader(reader).getAsJsonArray();

        List<TextSegment> textSegments = new ArrayList<>();
        jsonArray.forEach(jsonElement -> {
            Metadata metadata = new Metadata();
            TextSegment textSegment = new TextSegment(jsonElement.toString(), metadata);
            textSegments.add(textSegment);
        });

        return textSegments;
    }

    @Override
    public List<TextSegment> splitAll(List<Document> documents) {
        return DocumentSplitter.super.splitAll(documents);
    }
}
