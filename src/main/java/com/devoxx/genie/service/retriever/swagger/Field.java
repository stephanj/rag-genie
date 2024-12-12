package com.devoxx.genie.service.retriever.swagger;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

@Setter
@Getter
public class Field implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String parentPath;

    @JsonCreator
    public Field(@JsonProperty("name") String name, @JsonProperty("parentPath") String parentPath) {
        this.name = name;
        this.parentPath = normalizePath(parentPath);
    }

    private @NotNull String normalizePath(String path) {
        // Remove specific array indices and empty array brackets '[]'
        path = path.replaceAll("\\[\\d+]|\\[]", "");
        // Remove leading '.' that may appear after removals
        if (path.startsWith(".")) {
            path = path.substring(1);
        }
        return path;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Field field = (Field) o;
        return Objects.equals(name, field.name) && Objects.equals(parentPath, field.parentPath);
    }

    @Override
    public int hashCode() {
        return Objects.hash(name, parentPath);
    }

    @Override
    public String toString() {
        return "Field{name='" + name + "', parentPath='" + parentPath + "'}";
    }
}
