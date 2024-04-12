package com.devoxx.genie.web.rest;

public interface RestEndPoints {

    String API = "/api/";

    String API_REGISTER = API + "register";
    String API_ACCOUNT = API + "account";

    // Authenticated REST Endpoints
    String API_CONTENT = API + "content";
    String API_USERS = API + "users";
    String API_KEYS = API + "api-keys";
    String API_EMBEDDING_MODEL = API + "embedding-model";
    String API_EVALUATION = API + "evaluation";
    String API_EVALUATION_RESULT = API + "evaluation-result";
    String API_LANG_MODEL = API + "lang-model";
    String API_VECTOR_DOCUMENT = API + "vector-document";
    String API_CHAT_BOT = API + "chat";

    String API_AGENT_RESEARCHER = API + "agent-researcher";
}
