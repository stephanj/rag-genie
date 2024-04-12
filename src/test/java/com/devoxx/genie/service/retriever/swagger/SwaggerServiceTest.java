package com.devoxx.genie.service.retriever.swagger;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.Paths;
import io.swagger.v3.parser.OpenAPIV3Parser;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.URL;
import java.util.*;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class SwaggerServiceTest {

    @Test
    void test_getSwaggerInfo() {

        OpenAPI openAPI = new OpenAPIV3Parser().read("https://dvbe23.cfp.dev/v3/api-docs");

        assertThat(openAPI).isNotNull();

        // Now you can navigate the OpenAPI object to access API details
        System.out.println(openAPI.getInfo().getTitle());

        Paths paths = openAPI.getPaths();
        paths.forEach((path, pathItem) -> {
            System.out.println(path);
            pathItem.readOperationsMap().forEach((httpMethod, operation) -> {
                System.out.println(httpMethod);
                System.out.println(operation.getOperationId());
            });
        });
    }

    @Test
    void test_getSwaggerInfo_with_empty_url() {
        try {
            URL url = new URL("https://dvbe23.cfp.dev/api/public/talks");

            RestTemplate restTemplate = new RestTemplate();
            ResponseEntity<String> forEntity = restTemplate.getForEntity(url.toString(), String.class);

            JsonElement jsonElement = JsonParser.parseString(forEntity.getBody());
            Set<Field> fields = new HashSet<>();
            extractFields(jsonElement, "root", fields);

            // Print extracted fields
            for (Field field : fields) {
                System.out.println(field);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
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
