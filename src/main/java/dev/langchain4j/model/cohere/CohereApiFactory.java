package dev.langchain4j.model.cohere;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import dev.langchain4j.model.cohere.request.RequestLoggingInterceptor;
import dev.langchain4j.model.cohere.response.ResponseLoggingInterceptor;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.converter.scalars.ScalarsConverterFactory;

import java.time.Duration;

import static dev.langchain4j.internal.Utils.getOrDefault;

public class CohereApiFactory {

    Gson gson;
    OkHttpClient okHttpClient;
    private String baseUrl;

    public CohereApiFactory createHttpClient(String baseUrl,
                                             String apiKey,
                                             Duration timeout,
                                             boolean logRequests,
                                             boolean logResponses) {
        this.baseUrl = baseUrl;

        OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder()
            .addInterceptor(new AuthorizationInterceptor(apiKey))
            .callTimeout(timeout)
            .connectTimeout(timeout)
            .readTimeout(timeout)
            .writeTimeout(timeout);

        if (logRequests) {
            clientBuilder.addInterceptor(new RequestLoggingInterceptor());
        }

        if (logResponses) {
            clientBuilder.addInterceptor(new ResponseLoggingInterceptor());
        }

        this.okHttpClient = clientBuilder.build();

        return this;
    }

    CohereApiFactory createGson() {
        this.gson = new GsonBuilder()
            .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
            .setLenient()
            .create();
        return this;
    }

    CohereApi build() {
        Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(getOrDefault(baseUrl, CohereConfig.DEFAULT_BASE_URL))
            .client(okHttpClient)
            .addConverterFactory(ScalarsConverterFactory.create())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build();

        return retrofit.create(CohereApi.class);
    }
}
