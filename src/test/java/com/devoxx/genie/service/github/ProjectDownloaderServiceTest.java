package com.devoxx.genie.service.github;

import com.devoxx.genie.service.GenieServiceTest;
import com.devoxx.genie.service.retriever.github.ProjectDownloaderService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.File;
import java.io.IOException;


@GenieServiceTest
class ProjectDownloaderServiceTest {

    File tempFile;

    @Autowired
    ProjectDownloaderService projectDownloaderService;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("temp-", ".txt");
    }

    @AfterEach
    void tearDown() {
        tempFile.delete();
    }

    @Test
    void should_download_project() throws IOException {

        projectDownloaderService.getRepositoryContent(1L, "https://github.com/java/samples");
    }
}
