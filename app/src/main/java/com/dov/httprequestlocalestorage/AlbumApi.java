package com.dov.httprequestlocalestorage;

import java.util.List;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;

public class AlbumApi {
    public interface AlbumService {
       // For testing https://jsonplaceholder.typicode.com/photos?albumId=1
       @GET("photos")
        Call<List<Album>> getBooks(@Query("albumId") Integer id);
    }

    private final  static  String BaseUrl = "https://jsonplaceholder.typicode.com/";
    private static AlbumApi INSTANCE = null;
    private AlbumApi() {
    }
    public static AlbumApi getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new AlbumApi();
        }
        return INSTANCE;
    }

    public Retrofit getClient() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder().addInterceptor(interceptor).build();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BaseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .client(client)
                .build();
        return retrofit;
    }
}
