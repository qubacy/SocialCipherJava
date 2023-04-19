package com.mcdead.busycoder.socialcipher.api.vk.webinterface;

import static com.mcdead.busycoder.socialcipher.api.vk.VKAPIContext.C_API_VERSION;

import com.mcdead.busycoder.socialcipher.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.api.vk.gson.document.ResponseDocumentWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.photo.ResponsePhotoWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.update.longpoll.ResponseLongPollServerWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.video.ResponseVideoWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VKAPIAttachment {
    @GET("docs.getById?v=" + C_API_VERSION)
    Call<ResponseDocumentWrapper> getDocument(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token,
            @Query("docs") String docs
    );

    @GET("photos.getById?v=" + C_API_VERSION)
    Call<ResponsePhotoWrapper> getPhoto(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token,
            @Query("photos") String photos
    );

    @GET("video.get?v=" + C_API_VERSION)
    Call<ResponseVideoWrapper> getVideo(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token,
            @Query("videos") String videos
    );

    @GET("messages.getLongPollServer?v=" + C_API_VERSION)
    Call<ResponseLongPollServerWrapper> getLongPollServer(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );
}
