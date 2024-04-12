package com.devoxx.genie.web.rest.util;

import org.springframework.stereotype.Component;

import java.util.regex.Pattern;

@Component
public class HtmlCleanUpUtil {
    private static final Pattern pattern = Pattern.compile("<p(?: class=\"ql-align-justify\")?><br></p>");
    private static final Pattern spanPattern = Pattern.compile("<span[^>]*>|</span>");

    private HtmlCleanUpUtil() {
    }

    /**
     * Remove the HTML editor extra HTML tags.
     *
     * @param htmlString HTML string
     * @return cleaned up HTML string
     */
    public static String process(String htmlString) {
        var cleanedUp = pattern.matcher(htmlString).replaceAll("");
        var cleanedUpPart2 = spanPattern.matcher(cleanedUp).replaceAll("");

        // Check if the string ends with </p>
        boolean endsWithP = cleanedUpPart2.endsWith("</p>");

        // Remove the last </p> if present
        if (endsWithP) {
            cleanedUpPart2 = cleanedUpPart2.substring(0, cleanedUpPart2.length() - 4);
        }

        // Do the replacements
        cleanedUpPart2 = cleanedUpPart2.replace("<p>", "");
        cleanedUpPart2 = cleanedUpPart2.replace("</p>", "<br>");

        return cleanedUpPart2;
    }
}
