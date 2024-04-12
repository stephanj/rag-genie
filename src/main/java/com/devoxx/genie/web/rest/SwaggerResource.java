package com.devoxx.genie.web.rest;

import com.devoxx.genie.service.dto.RestEndpointDTO;
import com.devoxx.genie.service.retriever.swagger.Field;
import com.devoxx.genie.service.retriever.swagger.SwaggerService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api")
public class SwaggerResource {

    private static final Logger LOGGER = LoggerFactory.getLogger(SwaggerResource.class);

    private final SwaggerService swaggerService;

    public SwaggerResource(SwaggerService swaggerService) {
        this.swaggerService = swaggerService;
    }

    /**
     * Get REST endpoints using Swagger URL..
     *
     * @param url the query
     * @return list of REST endpoints
     */
    @GetMapping(value = "/swagger")
    public ResponseEntity<List<RestEndpointDTO>> getRESTEndPoints(@RequestParam("url") String url) {
        LOGGER.debug("The swagger enabled url: {}", url);
        return ResponseEntity.ok().body(swaggerService.getSwaggerInfo(url));
    }

    @GetMapping(value = "/swagger/fields")
    public ResponseEntity<List<Field>> getRESTFields(@RequestParam String url,
                                                     @RequestParam String endpoint) {
        LOGGER.debug("The REST fields");
        Set<Field> fieldsForRestEndpoint = swaggerService.getFieldsForRestEndpoint(url + endpoint);

        List<Field> sortedFields = new ArrayList<>(fieldsForRestEndpoint);
        sortedFields.sort(Comparator.comparing(Field::getParentPath).thenComparing(Field::getName));

        return ResponseEntity.ok().body(sortedFields);
    }
}
