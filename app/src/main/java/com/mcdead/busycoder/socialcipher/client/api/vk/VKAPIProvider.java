package com.mcdead.busycoder.socialcipher.client.api.vk;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.mcdead.busycoder.socialcipher.client.api.APIProvider;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentBase;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.ResponseAttachmentDeserializer;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.save.ResponseAttachmentDocSaveBody;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.save.ResponseAttachmentDocSaveBodyDeserializer;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIAttachment;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIChat;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIProfile;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIUploadAttachment;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VKAPIProvider implements APIProvider {
    public VKAPIChat generateChatAPI() {
        Gson responseAttachmentGson = new GsonBuilder().registerTypeAdapter(
                ResponseAttachmentBase.class,
                new ResponseAttachmentDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VKAPIContext.C_API_URL)
                .addConverterFactory(GsonConverterFactory.create(responseAttachmentGson))
                .build();

        return retrofit.create(VKAPIChat.class);
    }

    public VKAPIAttachment generateAttachmentAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VKAPIContext.C_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(VKAPIAttachment.class);
    }

    public VKAPIProfile generateProfileAPI() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VKAPIContext.C_API_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        return retrofit.create(VKAPIProfile.class);
    }

    public VKAPIUploadAttachment generateUploadAttachmentAPI() {
        Gson responseAttachmentDocSaveBodyGson = new GsonBuilder().registerTypeAdapter(
                ResponseAttachmentDocSaveBody.class,
                new ResponseAttachmentDocSaveBodyDeserializer()).create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(VKAPIContext.C_API_URL)
                .addConverterFactory(GsonConverterFactory.create(responseAttachmentDocSaveBodyGson))
                .build();

        return retrofit.create(VKAPIUploadAttachment.class);
    }
}
