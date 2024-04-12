package com.devoxx.genie.web.rest;

import com.devoxx.genie.domain.User;
import com.devoxx.genie.service.ContentImportService;
import com.devoxx.genie.service.ContentService;
import com.devoxx.genie.service.dto.ContentDTO;
import com.devoxx.genie.service.dto.SearchQueryDTO;
import com.devoxx.genie.service.dto.enumeration.ContentType;
import com.devoxx.genie.service.retriever.github.ProjectDownloaderService;
import com.devoxx.genie.service.retriever.pdf.PDFContentService;
import com.devoxx.genie.service.retriever.poi.PoiContentService;
import com.devoxx.genie.service.user.UserService;
import com.devoxx.genie.web.rest.errors.BadRequestAlertException;
import com.google.gson.JsonArray;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.time.ZonedDateTime;
import java.util.zip.ZipInputStream;

@RestController
@RequestMapping("/api")
public class ContentImportResource {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentImportResource.class);
    public static final String USER_NOT_FOUND = "User not found";
    public static final String USER_NOT_FOUND_CODE = "Usernotfound";
    private final ContentService contentService;
    private final ContentImportService contentImportService;
    private final UserService userService;
    private final ProjectDownloaderService projectDownloaderService;
    private final PDFContentService pdfContentService;
    private final PoiContentService poiContentService;

    public ContentImportResource(ContentService contentService,
                                 ContentImportService contentImportService,
                                 UserService userService,
                                 ProjectDownloaderService projectDownloaderService,
                                 PDFContentService pdfContentService,
                                 PoiContentService excelContentService) {
        this.contentService = contentService;
        this.contentImportService = contentImportService;
        this.userService = userService;
        this.projectDownloaderService = projectDownloaderService;
        this.pdfContentService = pdfContentService;
        this.poiContentService = excelContentService;
    }

    /**
     * POST /content/upload-text : Upload text content
     *
     * @param contentDTO the content to upload
     * @return the ResponseEntity with status 200 (OK) and with body the new content
     */
    @PostMapping("/content/upload-text")
    public ResponseEntity<ContentDTO> uploadText(@RequestBody ContentDTO contentDTO) {
        LOGGER.debug("Adding text content: {}", contentDTO);

        User user = userService.getAdminUser()
            .orElseThrow(() ->
                new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        contentDTO.setUserId(user.getId());

        ContentDTO savedContent = contentService.save(contentDTO);
        return ResponseEntity.ok().body(savedContent);
    }

    /**
     * POST /content/upload-url: Upload content from URL
     * @param contentDTO the content to upload
     * @return the ResponseEntity with status 200 (OK) and with body the new content
     */
    @PostMapping("/content/upload-url")
    public ResponseEntity<ContentDTO> uploadUrl(@RequestBody ContentDTO contentDTO) {
        LOGGER.debug("Adding URL content: {}", contentDTO);

        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        contentDTO.setUserId(user.getId());
        try {
            String webPageContent = contentImportService.getContentFromUrl(contentDTO.getSource());
            contentDTO.setValue(webPageContent);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }

        ContentDTO savedContent = contentService.save(contentDTO);
        return ResponseEntity.ok().body(savedContent);
    }

    /**
     * POST /content/search: Search for content using SerpAPI and import results
     * @param searchQuery the search query
     * @return the ResponseEntity with status 200 (OK) and with body the new content
     */
    @PostMapping("/content/search")
    public ResponseEntity<ContentDTO> searchWebPages(@RequestBody SearchQueryDTO searchQuery) {
        LOGGER.debug("Adding content via search: {}", searchQuery);

        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        contentImportService.searchWebPagesAndSaveContent(user.getId(), searchQuery);

        return ResponseEntity.ok().build();
    }

    /**
     * POST /content/github: Upload content from GitHub
     *
     * @param contentDTO the content to upload
     * @return the ResponseEntity with status 200 (OK) and with body the new content
     */
    @PostMapping(value = "/content/github")
    public ResponseEntity<ContentDTO> uploadGithub(@RequestBody ContentDTO contentDTO) {
        LOGGER.debug("Adding GitHub content: {}", contentDTO);

        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        contentDTO.setUserId(user.getId());

        try {
            projectDownloaderService.getRepositoryContent(user.getId(), contentDTO.getSource());

        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    /**
     * POST /content/upload-rest-data: Upload the REST API data
     *
     * @param contentDTO the REST endpoint content
     * @return the ResponseEntity with status 200 (OK) and with body the new content
     */
    @PostMapping(value = "/content/upload-rest-data")
    public ResponseEntity<ContentDTO> uploadRESTUrl(@RequestBody ContentDTO contentDTO) {
        LOGGER.debug("Uploading REST URL: {}", contentDTO);

        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        contentDTO.setUserId(user.getId());

        ContentDTO newContentDTO = new ContentDTO();

        contentImportService.addContentFromRestUrl(contentDTO)
            .ifPresent(jsonElement -> {
                JsonArray jsonArray = jsonElement.getAsJsonArray();
                newContentDTO.setValue(jsonArray.toString());
            });

        newContentDTO.setName(contentDTO.getName());
        newContentDTO.setDescription(contentDTO.getDescription());
        newContentDTO.setSource(contentDTO.getSource());
        newContentDTO.setUserId(user.getId());
        newContentDTO.setContentType(ContentType.JSON);
        ContentDTO saveContentDTO = contentService.save(newContentDTO);

        return ResponseEntity.ok().body(saveContentDTO);
    }

    /**
     * POST /content/file : Upload file content
     *
     * @param multipartFile the file to upload
     * @return the ResponseEntity with status 200 (OK) and with body the new content
     */
    @PostMapping("/content/file")
    public ResponseEntity<ContentDTO> uploadFile(@RequestParam MultipartFile multipartFile) {
        LOGGER.debug("Uploading file: {}", multipartFile);

        if (multipartFile.isEmpty() || multipartFile.getOriginalFilename() == null) {
            return ResponseEntity.badRequest().build();
        }

        String originalFilename = multipartFile.getOriginalFilename();
        String fileName = originalFilename.toLowerCase();

        User user = userService.getAdminUser()
            .orElseThrow(() -> new BadRequestAlertException(USER_NOT_FOUND, "USER", USER_NOT_FOUND_CODE));

        return processFileRequest(multipartFile, fileName, user);
    }

    /**
     * Process the file request
     *
     * @param multipartFile the uploaded file
     * @param fileName      the file name
     * @param user          the user
     * @return the ResponseEntity with status 200 (OK) and with body the new content
     */
    @NotNull
    private ResponseEntity<ContentDTO> processFileRequest(MultipartFile multipartFile,
                                                          String fileName,
                                                          User user) {
        LOGGER.debug("Processing file: {} for user {}", fileName, user.getLogin());

        try {
            ContentDTO contentDTO = new ContentDTO();

            String content = "";
            if (fileName.endsWith(".zip")) {
                var zipInputStream = new ZipInputStream(multipartFile.getInputStream());
                projectDownloaderService.processZipFile(user.getId(), fileName, zipInputStream);
                return ResponseEntity.ok().build();
            } else if (fileName.endsWith(".pdf")) {
                content = pdfContentService.getContent(multipartFile);
                contentDTO.setContentType(ContentType.PDF);
            } else if (fileName.endsWith(".xlsx") || fileName.endsWith(".xls")) {
                content = poiContentService.getContent(multipartFile);
                contentDTO.setContentType(ContentType.EXCEL);
            } else if (fileName.endsWith(".docx")) {
                content = poiContentService.getContent(multipartFile);
                contentDTO.setContentType(ContentType.DOC);
            } else if (fileName.endsWith(".txt")) {
                content = readFileAsString(multipartFile);
                contentDTO.setContentType(ContentType.TEXT);
            } else {
                return ResponseEntity.badRequest().build();
            }

            saveContent(multipartFile, contentDTO, content, user);
        } catch (IOException e) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }


    public static String readFileAsString(MultipartFile multipartFile) throws IOException {
        // Ensure the file is not empty
        if (multipartFile.isEmpty()) {
            throw new IOException("The file is empty");
        }

        StringBuilder fileContent = new StringBuilder();

        try (InputStream inputStream = multipartFile.getInputStream();
             BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                fileContent.append(line).append("\n");
            }
        } catch (IOException e) {
            // Handle exception if something goes wrong
            throw new IOException("Failed to read the file content", e);
        }

        return fileContent.toString();
    }

    /**
     * Save the content
     *
     * @param multipartFile the uploaded file
     * @param contentDTO    the content DTO
     * @param content       the content
     * @param user          the user
     */
    private void saveContent(MultipartFile multipartFile,
                             ContentDTO contentDTO,
                             String content,
                             User user) {
        LOGGER.debug("Saving content: {}", contentDTO);
        contentDTO.setName(multipartFile.getOriginalFilename());
        contentDTO.setValue(content.replace("\0", ""));
        contentDTO.setSource(multipartFile.getOriginalFilename());
        contentDTO.setUserId(user.getId());
        contentDTO.setCreatedOn(ZonedDateTime.now());
        contentService.save(contentDTO);
    }
}
