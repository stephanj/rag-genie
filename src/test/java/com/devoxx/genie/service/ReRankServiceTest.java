package com.devoxx.genie.service;

import com.devoxx.genie.service.dto.DocumentDTO;
import com.devoxx.genie.service.dto.enumeration.DocumentType;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ReRankServiceTest {

    @Test
    void reRank_json() {
        List<String> talks = List.of(
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

        ReRankService reRankService = new ReRankService();
        List<DocumentDTO> usedDocuments = new ArrayList<>();

        talks.forEach(talk ->
            usedDocuments.add(DocumentDTO.builder()
                                         .id("1")
                                         .text(talk)
                                         .score(0D)
                                         .docType(DocumentType.CONTENT)
                                         .build()));

        List<DocumentDTO> reRankDocuments = reRankService.reRankDocuments("Brian Goetz", usedDocuments);
        assertThat(reRankDocuments).isNotNull();

    }

    @Test
    void reRank_json_and_text() {
        List<String> talks = List.of(
            "{\"title\": \"Java 21\", \"speakers\": [{\"fullName\": \"Brian Goetz\", \"company\": \"Oracle\"}]}",
            "The ticket price for Devoxx Belgium is...",
            "{\"title\": \"IntelliJ Super Productivity in 45 Minutes\", \"speakers\": [{\"fullName\": \"Heinz Kabutz\", \"company\": \"JavaSpecialists.eu\"}]}",
            "{\"title\": \"Quarkus Community BOF - Devoxx.be Edition\", \"speakers\": [{\"fullName\": \"Dimitris Andreadis\", \"company\": \"Red Hat\"}]}",
            "{\"title\": \"Developer Unproductivity Horror Stories\", \"speakers\": [{\"fullName\": \"Trisha Gee\", \"company\": \"Gradle\"}, {\"fullName\": \"Helen Scott\", \"company\": \"JetBrains\"}]}"
        );

        ReRankService reRankService = new ReRankService();
        List<DocumentDTO> usedDocuments = new ArrayList<>();

        talks.forEach(talk -> usedDocuments.add(DocumentDTO.builder()
                                     .id("1")
                                     .text(talk)
                                     .docType(DocumentType.CONTENT)
                                     .build()));

        List<DocumentDTO> reRankDocuments = reRankService.reRankDocuments("Brian Goetz and ticket price", usedDocuments);
        assertThat(reRankDocuments).isNotNull();
    }
}
