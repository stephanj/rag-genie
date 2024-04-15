package com.devoxx.genie.web.rest.util;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import jakarta.annotation.PreDestroy;

@Component
public class HttpClientUtil {
    private static final Logger LOGGER = LoggerFactory.getLogger(HttpClientUtil.class);

    private static final OkHttpClient client = new OkHttpClient.Builder()
        .connectTimeout(180, TimeUnit.SECONDS)
        .readTimeout(180, TimeUnit.SECONDS)
        .build();

    /**
     * Performs a GET request to the given URL
     *
     * @param url the URL to perform the GET request
     * @return the response body
     * @throws IOException if an error occurs while performing the request
     */
    public String get(String url) throws IOException {
        return get(url, null);
    }

    /**
     * Performs a GET request to the given URL
     *
     * @param url         the URL to perform the GET request
     * @param queryParams the query parameters to add to the URL
     * @return the response body
     * @throws IOException if an error occurs while performing the request
     */
    public String get(String url, Map<String, String> queryParams) throws IOException {
        HttpUrl parse = HttpUrl.parse(url);
        if (parse == null) {
            LOGGER.error("Invalid URL: {}", url);
            throw new IOException("Invalid URL: " + url);
        }

        HttpUrl.Builder urlBuilder = parse.newBuilder();

        if (queryParams != null) {
            for (Map.Entry<String, String> entry : queryParams.entrySet()) {
                urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
            }
        }

        String newUrl = urlBuilder.build().toString();

        Request request = new Request.Builder()
            .url(newUrl)
            .get()
            .build();

        try (Response response = client.newCall(request).execute()) {
            if (!response.isSuccessful()) {
                LOGGER.error("Unexpected response code: {}", response);
                throw new IOException("Unexpected response code: " + response);
            }
            if (response.body() == null) {
                LOGGER.error("Response body is null");
                throw new IOException("Response body is null");
            }
            return response.body().string();
        }
    }

    @PreDestroy
    public void close() {
        client.dispatcher().executorService().shutdown();
        client.connectionPool().evictAll();
    }
}
