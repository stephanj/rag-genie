package com.devoxx.genie.service;

import com.devoxx.genie.service.dto.ContentDTO;
import com.devoxx.genie.service.retriever.swagger.Field;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

class ContentServiceTest {

    @Test
    void testMapping() {
        ContentImportService contentImportService = new ContentImportService(null, null);

        List<Field> fields = new ArrayList<>();
        fields.add(0, new Field("id", "root"));
        fields.add(1, new Field("title", "root"));
        fields.add(2, new Field("fullName", "root.speakers"));
        fields.add(3, new Field("company", "root.speakers"));

        ContentDTO contentDTO = new ContentDTO();
        contentDTO.setSource("https://dvbe23.cfp.dev/api/public/talks");
        contentDTO.setFields(fields);

        Optional<JsonElement> jsonElement = contentImportService.addContentFromRestUrl(contentDTO);

        if (jsonElement.isPresent()) {
            JsonElement jsonResult = jsonElement.get();
            JsonArray asJsonArray = jsonResult.getAsJsonArray();
            assertThat(asJsonArray.size()).isPositive();
        }
    }
}
