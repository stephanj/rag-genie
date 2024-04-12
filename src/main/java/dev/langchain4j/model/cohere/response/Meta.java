package dev.langchain4j.model.cohere.response;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Meta {
    public ApiVersion api_version;
    public BilledUnits billedUnits;

    public static class ApiVersion {
        String version;
    }
}
