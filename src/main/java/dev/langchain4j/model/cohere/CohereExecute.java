package dev.langchain4j.model.cohere;

import dev.langchain4j.model.cohere.callback.AsyncCallback;
import dev.langchain4j.model.cohere.exception.CohereException;
import retrofit2.Call;
import retrofit2.Response;

import java.io.IOException;

class CohereExecute {

    protected <T> T execute(Call<T> action) {
        try {
            Response<T> response = action.execute();
            if (response.isSuccessful()) {
                return response.body();
            } else {
                throw CohereException.fromResponse(response);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected <T> void execute(Call<T> action, AsyncCallback<T> callback) {
        action.enqueue(new retrofit2.Callback<T>() {
            @Override
            public void onResponse(Call<T> call, Response<T> response) {
                if (response.isSuccessful()) {
                    callback.onSuccess(response.body());
                } else {
                    callback.onFailure(CohereException.fromResponse(response));
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable throwable) {
                callback.onFailure(throwable);
            }
        });
    }
}
