package com.devoxx.genie.service;

import com.devoxx.genie.domain.Content;
import org.springframework.data.jpa.domain.Specification;

import jakarta.persistence.criteria.Path;

public class ContentSpecifications {

    public static Specification<Content> withNameMatching(String value, String matchMode) {
        return (root, query, criteriaBuilder) -> {
            if (value == null || matchMode == null) return null;

            Path<String> name = root.get("name");
            return switch (matchMode.toLowerCase()) {
                case "startswith" -> criteriaBuilder.like(name, value + "%");
                case "contains" -> criteriaBuilder.like(name, "%" + value + "%");
                case "endswith" -> criteriaBuilder.like(name, "%" + value);
                case "notcontains" -> criteriaBuilder.notLike(name, "%" + value + "%");
                case "equals" -> criteriaBuilder.equal(name, value);
                case "notequals" -> criteriaBuilder.notEqual(name, value);
                default -> null; // or throw an IllegalArgumentException if that's preferred
            };
        };
    }
}
