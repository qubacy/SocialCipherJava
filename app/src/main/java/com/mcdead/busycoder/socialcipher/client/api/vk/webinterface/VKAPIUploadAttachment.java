package com.mcdead.busycoder.socialcipher.client.api.vk.webinterface;

import static com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIContext.C_API_VERSION;

import com.mcdead.busycoder.socialcipher.client.api.vk.VKAPIContext;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.getserver.ResponseAttachmentDocUploadServerWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.getserver.ResponseAttachmentPhotoUploadServerWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.save.ResponseAttachmentDocSaveWrapper;
import com.mcdead.busycoder.socialcipher.client.api.vk.gson.chat.attachment.upload.save.ResponseAttachmentPhotoSaveWrapper;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface VKAPIUploadAttachment {
    @GET("docs.getMessagesUploadServer?v=" + C_API_VERSION)
    Call<ResponseAttachmentDocUploadServerWrapper> getDocUploadServer(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token,
            @Query("peer_id") long peerId
    );

    @GET("photos.getMessagesUploadServer?v=" + C_API_VERSION)
    Call<ResponseAttachmentPhotoUploadServerWrapper> getPhotoUploadServer(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token,
            @Query("peer_id") long peerId
    );

    @GET("docs.save?v=" + C_API_VERSION)
    Call<ResponseAttachmentDocSaveWrapper> saveUploadedDoc(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token,
            @Query("file") String file
    );

    @GET("photos.save?v=" + C_API_VERSION)
    Call<ResponseAttachmentPhotoSaveWrapper> saveUploadedPhoto(
            @Query(VKAPIContext.C_ACCESS_TOKEN_PROP_NAME) String token,
            @Query("album_id") long albumId,
            @Query("photos_list") String photo,
            @Query("server") long serverId,
            @Query("hash") String hash
    );
}
