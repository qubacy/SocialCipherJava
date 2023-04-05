package com.mcdead.busycoder.socialcipher.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItemDeserializer;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIStore {
    private static VKAPIInterface s_api = null;

    public static synchronized VKAPIInterface getAPIInstance() {
        return s_api;
    }

    public static void init() {
        Gson gson = new GsonBuilder().registerTypeAdapter(
                ResponseDialogItem.class,
                new ResponseDialogItemDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VKAPIContext.C_API_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        s_api = retrofit.create(VKAPIInterface.class);
    }
}
