package com.devoxx.genie.service.retriever.github;

import com.devoxx.genie.service.ContentService;
import com.devoxx.genie.service.dto.ContentDTO;
import com.devoxx.genie.service.dto.enumeration.ContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

@Service
public class ProjectDownloaderService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ProjectDownloaderService.class);

    private static final Set<String> EXCLUDED_DIRS =
        new HashSet<>(Arrays.asList("__MACOSX", "docs", "examples", "test", "tests", "build", "target", "bin"));
    private static final Set<String> UTILITY_OR_CONFIG_FILES =
        new HashSet<>(Arrays.asList("pom.xml", "build.gradle", "settings.gradle"));
    private static final Set<String> GITHUB_WORKFLOW_OR_DOCS =
        new HashSet<>(Arrays.asList("README.md", ".github"));
    private static final int MIN_LINE_COUNT = 10;
    private final ContentService contentService;

    public ProjectDownloaderService(ContentService contentService) {
        this.contentService = contentService;
    }

    public boolean isJavaFile(String filePath) {
        return filePath.endsWith(".java");
    }

    public boolean isLikelyUsefulFile(String filePath) {
        String[] parts = filePath.split(File.pathSeparator);
        for (String part : parts) {
            if (part.startsWith(".")) {
                return false;
            }
        }
        if (filePath.toLowerCase().contains("test")) {
            return false;
        }
        for (String excludedDir : EXCLUDED_DIRS) {
            if (filePath.contains(File.pathSeparator + excludedDir + File.pathSeparator) || filePath.startsWith(excludedDir + File.pathSeparator)) {
                return false;
            }
        }
        for (String fileName : UTILITY_OR_CONFIG_FILES) {
            if (filePath.contains(fileName)) {
                return false;
            }
        }
        for (String docFile : GITHUB_WORKFLOW_OR_DOCS) {
            if (filePath.contains(docFile)) {
                return false;
            }
        }
        return true;
    }

    public boolean isTestFile(String fileContent) {
        String[] testIndicators = {"@Test", "import org.junit."};
        for (String indicator : testIndicators) {
            if (fileContent.contains(indicator)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasSufficientContent(String fileContent) {
        String[] lines = fileContent.split("\n");
        int substantiveLines = 0;
        for (String line : lines) {
            String trimmedLine = line.trim();
            if (!trimmedLine.isEmpty() && !trimmedLine.startsWith("//")) {
                substantiveLines++;
            }
        }
        return substantiveLines >= MIN_LINE_COUNT;
    }

    public void getRepositoryContent(Long userId, String repoUrl) throws IOException {
        URL url = new URL(repoUrl + "/archive/master.zip");
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");

        int responseCode = connection.getResponseCode();
        if (responseCode == HttpURLConnection.HTTP_OK) {
            var zipInputStream = new ZipInputStream(connection.getInputStream());
            processZipFile(userId, "master.zip", zipInputStream);
        } else {
            LOGGER.error("Error: {}", responseCode);
        }
    }

    public void processZipFile(Long userId,
                               String fileName,
                               ZipInputStream zipInputStream) throws IOException {

        ZipEntry entry;
        while ((entry = zipInputStream.getNextEntry()) != null) {
            StringBuilder fileContent = new StringBuilder(32_000);

            String filePath = entry.getName();
            if (entry.isDirectory() || !isJavaFile(filePath) || !isLikelyUsefulFile(filePath)) {
                continue;
            }

            BufferedReader reader = new BufferedReader(new InputStreamReader(zipInputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }

            if (isTestFile(fileContent.toString()) || !hasSufficientContent(fileContent.toString())) {
                continue;
            }

            try {
                // String cleanedContent = removeCommentsAndDocstrings(fileContent.toString());
                LOGGER.debug("Processing file: {}", filePath);
                createContentItem(userId, fileName, filePath, fileContent);
            } catch (Exception e) {
                // Skip files with syntax errors
            }
        }
    }

    /**
     * Create a content item from the file content.
     *
     * @param userId      the user ID
     * @param fileName    the file name
     * @param filePath    the file path
     * @param fileContent the file content
     */
    private void createContentItem(Long userId, String fileName, String filePath, StringBuilder fileContent) {
        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setName(filePath);
        contentDTO.setValue(fileContent.toString());
        contentDTO.setSource(fileName);
        contentDTO.setContentType(ContentType.CODE);
        contentDTO.setUserId(userId);
        contentDTO.setCreatedOn(ZonedDateTime.now());
        contentService.save(contentDTO);
    }
}
