package com.mcdead.busycoder.socialcipher.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.api.vk.VKAPIInterface;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.save.ResponseAttachmentDocSaveBody;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.save.ResponseAttachmentDocSaveBodyDeserializer;
import com.mcdead.busycoder.socialcipher.api.vk.gson.attachment.upload.save.ResponseAttachmentDocSaveWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItem;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogItemDeserializer;
import com.mcdead.busycoder.socialcipher.error.Error;
import com.mcdead.busycoder.socialcipher.setting.network.SettingsNetwork;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class APIStore {
    private static Object s_api = null;

    public static synchronized Object getAPIInstance() {
        return s_api;
    }

    public static Error init() {
        SettingsNetwork settingsNetwork = SettingsNetwork.getInstance();

        if (settingsNetwork == null)
            return new Error("Settings Network haven't been initialized!", true);
        if (settingsNetwork.getAPIType() == null)
            return new Error("API Type had a value of null!", true);

        switch (settingsNetwork.getAPIType()) {
            case VK: return initVK();
        }

        return new Error("Provided API Type is invalid!", true);
    }

    private static Error initVK() {
        Gson responseDialogGson = new GsonBuilder().registerTypeAdapter(
                ResponseDialogItem.class,
                new ResponseDialogItemDeserializer()).create();
        Gson responseAttachmentDocSaveBodyGson = new GsonBuilder().registerTypeAdapter(
                ResponseAttachmentDocSaveBody.class,
                new ResponseAttachmentDocSaveBodyDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VKAPIContext.C_API_URL)
                .addConverterFactory(GsonConverterFactory.create(responseDialogGson))
                .addConverterFactory(GsonConverterFactory.create(responseAttachmentDocSaveBodyGson))
                .build();

        s_api = retrofit.create(VKAPIInterface.class);

        return null;
    }
}
