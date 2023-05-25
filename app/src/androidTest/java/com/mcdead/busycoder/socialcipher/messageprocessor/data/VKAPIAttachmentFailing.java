package com.mcdead.busycoder.socialcipher.messageprocessor.data;

import com.mcdead.busycoder.socialcipher.client.api.vk.gson.document.ResponseDocumentWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.photo.ResponsePhotoWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.update.longpoll.ResponseLongPollServerWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.video.ResponseVideoWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.webinterface.VKAPIAttachment;

import retrofit2.Call;

public class VKAPIAttachmentFailing implements VKAPIAttachment {

    @Override
    public Call<ResponseDocumentWrapper> getDocument(
            final String token,
            final String docs)
    {


        return null;
    }

    @Override
    public Call<ResponsePhotoWrapper> getPhoto(
            final String token,
            final String photos)
    {
        return null;
    }

    @Override
    public Call<ResponseVideoWrapper> getVideo(
            final String token,
            final String videos)
    {
        return null;
    }

    @Override
    public Call<ResponseLongPollServerWrapper> getLongPollServer(String token) {
        return null;
    }
}
