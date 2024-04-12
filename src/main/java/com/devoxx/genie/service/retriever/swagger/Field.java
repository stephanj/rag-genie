package com.devoxx.genie.service.retriever.swagger;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class Field implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String name;
    private String parentPath;

    public Field(String name, String parentPath) {
        // Normalize the path to ignore array indices and 'root[]'
        this.name = name;
        this.parentPath = normalizePath(parentPath);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getParentPath() {
        return parentPath;
    }

    public void setParentPath(String parentPath) {
        this.parentPath = parentPath;
    }

    private String normalizePath(String path) {
        // Remove specific array indices and empty array brackets '[]'
        path = path.replaceAll("\\[\\d+\\]|\\[\\]", "");
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
