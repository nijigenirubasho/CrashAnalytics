package zqy.gp.crashanalytics.net.github;

import android.util.Log;

import java.io.IOException;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class GithubServiceCreator {

    private static final String TAG = "GithubServiceCreator";

    private static final String BASE_URL = "https://api.github.com/";

    private GithubServiceCreator() {
    }

    public static GithubService create(final String accessToken) {
        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(chain -> {
                    Request request = chain.request();
                    Request newRequest = request.newBuilder()
                            .addHeader("Authorization", "token " + accessToken)
                            .build();
                    Log.d(TAG, "intercept: request = " + request);
                    return chain.proceed(newRequest);
                }).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit.create(GithubService.class);
    }
}
