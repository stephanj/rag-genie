package com.devoxx.genie.service.retriever.swagger;

import com.devoxx.genie.service.dto.RestEndpointDTO;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;

@Service
public class SwaggerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerService.class);
    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * Get the swagger info from the given URL
     *
     * @param url the URL to get the swagger info from
     * @return the list of rest endpoints
     */
    public List<RestEndpointDTO> getSwaggerInfo(String url) {
        String swaggerURl = url + "/v3/api-docs";
        LOGGER.debug("Get the swagger info from the given URL: {}", swaggerURl);

        OpenAPI openAPI = new OpenAPIV3Parser().read(swaggerURl);

        Paths paths = openAPI.getPaths();

        if (paths.isEmpty()) {
            return List.of();
        }

        List<RestEndpointDTO> restEndpoints = new ArrayList<>();

        paths
            .forEach((path, pathItem) ->
                pathItem.readOperationsMap().forEach((httpMethod, operation) -> {

                    // Only handle GET methods
                    if (!httpMethod.name().equals("GET")) {
                        return;
                    }

                    List<String> params = new ArrayList<>();
                    if (operation.getParameters() != null) {
                        operation.getParameters().forEach(parameter -> {
                            LOGGER.debug("Parameter: {}", parameter.getName());
                            params.add(parameter.getName());
                        });
                    }
                    // We can't introspect the response body if there are parameters with this approach.
                    if (params.isEmpty() || params.getFirst().equals("pageable")) {
                        Optional<Set<String>> jsonKeys = getJSONKeys(url + path);
                        jsonKeys.ifPresent(keys ->
                            restEndpoints.add(
                                RestEndpointDTO.builder()
                                .path(path)
                                .method(httpMethod.name())
                                .operationId(operation.getOperationId())
                                .params(params)
                                .build()));
                    }

                }));

        return restEndpoints;
    }

    private Optional<Set<String>> getJSONKeys(String restEndPoint) {

        LOGGER.debug(">>> Get the JSON keys from the given response entity: {}", restEndPoint);

        ResponseEntity<String> forEntity = restTemplate.getForEntity(restEndPoint, String.class);

        if (forEntity.getBody() == null) {
            return Optional.empty();
        }

        // Parse JSON response
        JsonElement jsonElement = JsonParser.parseString(forEntity.getBody());
        if (jsonElement.isJsonObject()) {
            // The response is a single object
            JsonObject jsonObject = jsonElement.getAsJsonObject();
            // Process the object
            LOGGER.debug("Single object received: {}", jsonObject);
        } else if (jsonElement.isJsonArray()) {
            // The response is an array
            JsonArray jsonArray = jsonElement.getAsJsonArray();

            if (!jsonArray.isEmpty()) {
                // Process the array
                JsonElement jsonArrayElement = jsonArray.get(0);

                JsonObject asJsonObject = jsonArrayElement.getAsJsonObject();
                LOGGER.debug("Array received: {}", jsonArrayElement.getAsJsonObject());

                return Optional.of(asJsonObject.keySet());
            }
        } else {
            LOGGER.debug("Received JSON is neither an object nor an array!");
        }

        return Optional.empty();
    }


    /**
     * Get all the fields for the given REST endpoint (recursively).
     *
     * @param restEndPoint the restEndPoint to get the JSON keys from
     * @return the set of JSON keys
     */
    public Set<Field> getFieldsForRestEndpoint(String restEndPoint) {

        LOGGER.debug(">>> Get the JSON keys from the given response entity: {}", restEndPoint);

        ResponseEntity<String> forEntity = restTemplate.getForEntity(restEndPoint, String.class);

        JsonElement jsonElement = JsonParser.parseString(forEntity.getBody());
        Set<Field> fields = new HashSet<>();
        extractFields(jsonElement, "root", fields);

        return fields;
    }

    private void extractFields(JsonElement element, String parentPath, Set<Field> fields) {
        if (element.isJsonObject()) {
            JsonObject obj = element.getAsJsonObject();
            for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                String currentPath = parentPath + "." + entry.getKey();
                fields.add(new Field(entry.getKey(), parentPath));

                extractFields(entry.getValue(), currentPath, fields);
            }
        } else if (element.isJsonArray()) {
            JsonArray array = element.getAsJsonArray();
            String arrayPath = parentPath + "[]"; // Remove specific array indices
            for (JsonElement jsonElement : array) {
                extractFields(jsonElement, arrayPath, fields);
            }
        }
    }
}
