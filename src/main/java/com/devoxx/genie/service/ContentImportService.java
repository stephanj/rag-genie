package com.devoxx.genie.service;

import com.devoxx.genie.service.dto.ContentDTO;
import com.devoxx.genie.service.dto.SearchQueryDTO;
import com.devoxx.genie.service.dto.enumeration.ContentType;
import com.devoxx.genie.service.retriever.swagger.Field;
import com.google.gson.*;
import dev.langchain4j.data.document.Document;
import dev.langchain4j.data.document.DocumentLoader;
import dev.langchain4j.data.document.DocumentParser;
import dev.langchain4j.data.document.parser.TextDocumentParser;
import dev.langchain4j.data.document.source.UrlSource;
import dev.langchain4j.data.document.transformer.HtmlTextExtractor;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URI;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class ContentImportService {
    private static final Logger LOGGER = LoggerFactory.getLogger(ContentImportService.class);

    private final ContentService contentService;
    private final WebSearchService webSearchService;

    public ContentImportService(ContentService contentService,
                                WebSearchService webSearchService) {
        this.contentService = contentService;
        this.webSearchService = webSearchService;
    }

    /**
     * Add content from a URL.
     *
     * @param webPageUrl the web page URL
     * @throws MalformedURLException the malformed URL exception
     */
    public String getContentFromUrl(String webPageUrl) throws MalformedURLException {
        LOGGER.debug("Add content from url : {}", webPageUrl);

        UrlSource urlSource = new UrlSource(URI.create(webPageUrl).toURL());

        DocumentParser documentParser = new TextDocumentParser();
        Document document = DocumentLoader.load(urlSource, documentParser);

        HtmlTextExtractor htmlTextExtractor = new HtmlTextExtractor();
        Document htmlContent = htmlTextExtractor.transform(document);

        return htmlContent.text().replace("\n", " ")
                                 .replaceAll("\\s+", " ");
    }

    /**
     * Add content from a REST URL.
     *
     * @param contentDTO the content DTO
     * @return the JSON element
     */
    public Optional<JsonElement> addContentFromRestUrl(ContentDTO contentDTO) {
        LOGGER.debug("Add content from REST url : {}", contentDTO.getSource());

        String jsonResponse = new RestTemplate().getForObject(contentDTO.getSource(), String.class);
        if (jsonResponse == null) {
            throw new RuntimeException("No response from the REST URL");
        }

        JsonElement jsonElement = JsonParser.parseString(jsonResponse);

        // Initialize metaFields here to ensure it's accessible in both branches
        List<Field> fields = contentDTO.getFields();

        if (jsonElement.isJsonObject()) {
            return Optional.of(filterJsonObject(jsonElement.getAsJsonObject(), fields));
        } else if (jsonElement.isJsonArray()) {
            return Optional.of(processJsonArray(jsonElement.getAsJsonArray(), fields));
        } else {
            LOGGER.debug("Received JSON is neither an object nor an array!");
            return Optional.empty();
        }
    }

    /**
     * Process a JSON array.
     * @param jsonArray  the JSON array
     * @param metaFields the meta-fields
     * @return the JSON array
     */
    private JsonArray processJsonArray(JsonArray jsonArray, List<Field> metaFields) {
        JsonArray resultArray = new JsonArray();
        jsonArray.forEach(jsonElement -> {
            if (jsonElement.isJsonObject()) {
                jsonElement = extractAndFilterFields(jsonElement.getAsJsonObject(), "root", metaFields);
                resultArray.add(jsonElement);
                LOGGER.debug("Filtered Array received: {}", jsonElement);
            } else {
                LOGGER.debug("Received JSON array item is not an object!");
            }
        });
        // Assuming you want the entire array as the value
        return resultArray;
    }

    /**
     * Filter a JSON object.
     * @param jsonObject the JSON object
     * @param metaFields the meta-fields
     * @return the JSON object
     */
    private JsonObject filterJsonObject(JsonObject jsonObject, List<Field> metaFields) {
        JsonObject filteredJson = new JsonObject();
        metaFields.forEach(field -> {
            JsonElement jsonElement = jsonObject.get(field.getName());
            if (jsonElement.isJsonPrimitive()) {
                String value = jsonObject.getAsJsonPrimitive(field.getName()).getAsString();
                filteredJson.addProperty(field.getName(), removeHTML(value));
            } else {
                if (!jsonElement.isJsonNull()) {
                    JsonArray asJsonArray = jsonElement.getAsJsonArray();
                    String asString = asJsonArray.toString();
                    filteredJson.addProperty(field.getName(), removeHTML(asString));
                } else {
                    LOGGER.debug("json object is null");
                }
            }
        });
        return filteredJson;
    }

    /**
     * Extract and filter fields from a JSON element.
     * @param element the JSON element
     * @param currentPath the current path
     * @param metaFields the meta-fields
     * @return the JSON element
     */
    private JsonElement extractAndFilterFields(JsonElement element,
                                               String currentPath,
                                               List<Field> metaFields) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            JsonObject newObj = new JsonObject();
            processObject(currentPath, metaFields, obj, newObj);
            return newObj;
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            JsonArray newArray = new JsonArray();
            processArray(currentPath, metaFields, array, newArray);
            return newArray;
        }
        // Return JsonNull if the element does not match any criteria
        return JsonNull.INSTANCE;
    }

    /**
     * Process an array.
     * @param currentPath the current path
     * @param metaFields the meta-fields
     * @param array the array
     * @param newArray the new array
     */
    private void processArray(String currentPath,
                              List<Field> metaFields,
                              JsonArray array,
                              JsonArray newArray) {
        for (int i = 0; i < array.size(); i++) {
            JsonElement filteredElement = extractAndFilterFields(array.get(i), currentPath, metaFields);
            if (!filteredElement.isJsonNull() && !((JsonObject) filteredElement).isEmpty()) {
                newArray.add(filteredElement);
            }
        }
    }

    /**
     * Process an object.
     * @param currentPath the current path
     * @param metaFields the meta-fields
     * @param obj the object
     * @param newObj the new object
     */
    private void processObject(String currentPath,
                               List<Field> metaFields,
                               JsonObject obj,
                               JsonObject newObj) {
        for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
            String newPath = currentPath.isEmpty() ? entry.getKey() : currentPath + "." + entry.getKey();

            if (entry.getValue().isJsonArray() || entry.getValue().isJsonObject()) {
                JsonElement filteredElement = extractAndFilterFields(entry.getValue(), newPath, metaFields);

                if (!filteredElement.isJsonNull() && isMeta(newPath, metaFields)) {
                    newObj.add(entry.getKey(), filteredElement);
                }
            } else if (isMetaField(newPath, metaFields)) {
                newObj.add(entry.getKey(), entry.getValue());
            }
        }
    }

    /**
     * Check if a field is a meta field.
     * @param parentPath the parent path
     * @param metaFields the meta-fields
     * @return true if the field is a meta field, false otherwise
     */
    private boolean isMetaField(String parentPath, List<Field> metaFields) {
        for (Field metaField : metaFields) {
            if (parentPath.equals(metaField.getParentPath() + "." + metaField.getName())) {
                return true;
            }
        }
        return false;
    }

    private boolean isMeta(String parentPath, List<Field> metaFields) {
        for (Field metaField : metaFields) {
            if (parentPath.equals(metaField.getParentPath())) {
                return true;
            }
        }
        return false;
    }

    @NotNull
    private static String removeHTML(String value) {
        // Check if value has HTML Tags
        if (value.contains("<") && value.contains(">")) {
            // Remove HTML Tags
            value = value.replaceAll("<.*?>", "");
        }
        return value;
    }

    /**
     * Search web pages for a query and import the web page content for the first x results.
     * @param userId the user id
     * @param searchQuery the search query
     */
    public void searchWebPagesAndSaveContent(Long userId, SearchQueryDTO searchQuery) {
        LOGGER.debug("Search web pages for query: {}", searchQuery);

        webSearchService.retrieve(userId, searchQuery.getQuery(), searchQuery.getTotalResults())
            .forEach(searchResultDTO -> {
                LOGGER.debug("Save web page content fo r: {}", searchResultDTO.getLink());

                try {
                    String contentFromUrl = getContentFromUrl(searchResultDTO.getLink());
                    ContentDTO contentDTO = new ContentDTO();
                    contentDTO.setUserId(userId);
                    contentDTO.setSource(searchResultDTO.getLink());
                    contentDTO.setName(searchQuery.getQuery());
                    contentDTO.setContentType(ContentType.HTML);
                    contentDTO.setValue(contentFromUrl);
                    contentDTO.setDescription(searchResultDTO.getSnippet());
                    contentService.save(contentDTO);
                } catch (MalformedURLException | RuntimeException e) {
                    throw new RuntimeException(e);
                }
            });
    }
}
