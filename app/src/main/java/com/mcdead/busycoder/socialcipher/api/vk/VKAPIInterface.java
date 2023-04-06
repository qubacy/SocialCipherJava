package com.mcdead.busycoder.socialcipher.api.vk;

import com.mcdead.busycoder.socialcipher.api.vk.gson.dialog.ResponseDialogWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.dialogs.ResponseDialogsWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.document.ResponseDocumentWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.longpoll.ResponseLongPollServerWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.photo.ResponsePhotoWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.user.ResponseUserWrapper;
import com.mcdead.busycoder.socialcipher.api.vk.gson.video.ResponseVideoWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VKAPIInterface /*extends APIInterface*/ {
    @GET("users.get?v=5.131")
    Call<ResponseUserWrapper> localUser(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("users.get?v=5.131")
    Call<ResponseUserWrapper> user(
            @Query("user_ids") long userId,
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("messages.getConversations?v=5.131&extended=1&fields=name")
    Call<ResponseDialogsWrapper> dialogs(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("messages.getHistory?v=5.131")
    Call<ResponseDialogWrapper> dialog(
            @Query("peer_id") long peerId,
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );

    @GET("docs.getById?v=5.131")
    Call<ResponseDocumentWrapper> document(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token,
            @Query("docs") String docs
    );

    @GET("photos.getById?v=5.131")
    Call<ResponsePhotoWrapper> photo(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token,
            @Query("photos") String photos
    );

    @GET("video.get?v=5.131")
    Call<ResponseVideoWrapper> video(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token,
            @Query("videos") String videos
    );

    @GET("messages.getLongPollServer?v=5.131")
    Call<ResponseLongPollServerWrapper> longPollServer(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token
    );
}
