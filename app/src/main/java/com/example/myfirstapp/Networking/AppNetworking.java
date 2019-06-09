package com.example.myfirstapp.Networking;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import androidx.annotation.Nullable;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

import static android.content.ContentValues.TAG;

public class AppNetworking {

    private AppNetworking() {}
    private static class AppNetworkingLoader {
        private static final AppNetworking INSTANCE = new AppNetworking();
    }

    public static AppNetworking getInstance() {
        return AppNetworkingLoader.INSTANCE;
    }

    public static final String baseUrl = "http://192.168.31.194:8000/app/";
    public String token = null;

    public interface CallBack {
        void onFailure(int code, String msg);
        void onSuccess(String json);
    }

    public void get(String path, final CallBack callback) {

        String url = "http://192.168.31.194:8000/app/" + path;

        OkHttpClient okHttpClient = new OkHttpClient();
        final Request request = new Request.Builder().url(url).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "onFailure: " + e.getMessage());
                callback.onFailure(-1, e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                Log.d("TAG", "onResponse: " + response.body().string());
                String json = response.body().string();
                callback.onSuccess(json);
            }
        });

    }

    public void post(String path, @Nullable Map<String, String> parameter, final CallBack callback) {

        String url = "http://192.168.31.194:8000/app/" + path;

        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(10, TimeUnit.SECONDS)
                .writeTimeout(10, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .build();

        FormBody.Builder requestBodyPost = new FormBody.Builder();
        if (parameter != null) {
            for (Map.Entry<String, String> entry : parameter.entrySet()) {
                requestBodyPost.add(entry.getKey(),entry.getValue());
            }
        }
        if (token != null) {
            requestBodyPost.add("token", token);
        }
        final Request request = new Request.Builder().url(url).post(requestBodyPost.build()).build();

        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.d("TAG", "onFailure: " + e.getMessage());
                callback.onFailure(-1, e.getLocalizedMessage());
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String json = response.body().string();
                Log.d("TAG", "onResponse: " + json);
                callback.onSuccess(json);
            }
        });

    }

    public void uploadImage(File file, String RequestURL) {

        try {

            final MediaType MEDIA_TYPE_PNG = MediaType.parse("image/*");

            RequestBody requestBody = new MultipartBody.Builder()
                    .setType(MultipartBody.FORM)
                    .addFormDataPart("avatar", "avatar.png", RequestBody.create(MEDIA_TYPE_PNG, file))
                    .build();

            Request request = new Request.Builder()
                    .url(RequestURL)
                    .post(requestBody)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Response response = client.newCall(request).execute();

            Log.d("response", "uploadImage:"+response.body().string());

        } catch (UnknownHostException | UnsupportedEncodingException e) {
            Log.e(TAG, "Error: " + e.getLocalizedMessage());
        } catch (Exception e) {
            Log.e(TAG, "Other Error: " + e.getLocalizedMessage());
        }
    }

}
