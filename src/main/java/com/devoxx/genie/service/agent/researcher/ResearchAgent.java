package com.devoxx.genie.service.agent.researcher;

import dev.langchain4j.service.SystemMessage;
import dev.langchain4j.service.UserMessage;
import dev.langchain4j.service.V;

import java.util.List;

public interface ResearchAgent {
    @SystemMessage("You are a world-class researcher")
    @UserMessage("""
        Generate 3 Google search queries to gather information on the subtopic '{{topic}}'.
        Do not number them, only provide the queries.
        """)
    List<String> getSearchQueries(@V("topic") String topic);

    @SystemMessage("You are a world-class researcher")
    @UserMessage("""
        Generate a comprehensive report on the topic '{{topic}}' by using the context provided below.
        Ensure that the final report is well-structured, coherent, and covers all the important aspects of the topic.
        Make sure that it includes EVERYTHING in each of the reports, in a better structured, more info-heavy manner.
        Nothing -- absolutely nothing, should be left out.
        If you forget to include ANYTHING from any of the previous reports, you will face the consequences.
        Include a table of contents.
        Leave nothing out. Use Markdown for formatting.
        Context:
        {{context}}
        """)
    String getReport(@V("topic") String topic, @V("context") String context);
}
